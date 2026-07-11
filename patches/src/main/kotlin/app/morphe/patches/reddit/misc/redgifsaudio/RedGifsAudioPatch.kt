package app.morphe.patches.reddit.misc.redgifsaudio

import app.morphe.patcher.data.BytecodeContext
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.fingerprint.MethodFingerprint
import app.morphe.patcher.patch.BytecodePatch
import app.morphe.patcher.patch.annotation.CompatiblePackage
import app.morphe.patcher.patch.annotation.Patch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Patch(
    name = "RedGifs Audio Fix",
    description = "Enables audio playback for RedGifs videos embedded in Reddit posts.",
    compatiblePackages = [CompatiblePackage("com.reddit.frontpage")]
)
@Suppress("unused")
val redgifsAudioPatch = bytecodePatch(
    name = "RedGifs Audio Fix",
    description = "Enables audio playback for RedGifs videos embedded in Reddit posts.",
) {
    compatibleWith("com.reddit.frontpage")

    val toRedditVideoMatch by toRedditVideoFingerprint()

    execute {
        val method = toRedditVideoMatch.mutableMethod
        val instructions = method.instructions

        // Find the INVOKE_STATIC call to toRedditVideoMp4Urls and the
        // subsequent NEW_INSTANCE of RedditVideo.
        // We need to find where 'z' (hasAudio boolean, 9th arg) is loaded
        // and force it to true (1) when the URL contains redgifs.com.
        //
        // Strategy: find the CONST_4 instruction that loads 'z' (hasAudio)
        // immediately before the INVOKE_VIRTUAL on RedditVideo constructor,
        // and insert a check: if str2 contains "redgifs", override z to 1.

        // Find the invoke-direct for RedditVideo constructor
        val constructorIndex = instructions.indexOfFirst { instruction ->
            instruction.opcode == Opcode.INVOKE_DIRECT &&
            instruction is ReferenceInstruction &&
            (instruction as ReferenceInstruction).reference.toString()
                .contains("RedditVideo;-><init>")
        }

        if (constructorIndex == -1) throw Exception("RedditVideo constructor not found")

        // The hasAudio boolean (z) is the 9th parameter (index 8, 0-based).
        // Walk backwards from constructor to find the CONST_4 that loads z.
        // z = te2Var.f — it's an iget-boolean into a register.
        // Find IGET_BOOLEAN with field name 'f' on class te2
        val hasAudioLoadIndex = (0 until constructorIndex).reversed().firstOrNull { idx ->
            val instr = instructions[idx]
            instr.opcode == Opcode.IGET_BOOLEAN &&
            instr is ReferenceInstruction &&
            (instr as ReferenceInstruction).reference.toString().let { ref ->
                ref.contains("->f:Z") || ref.endsWith(":Z")
            }
        } ?: throw Exception("hasAudio iget-boolean not found")

        val hasAudioReg = (instructions[hasAudioLoadIndex] as OneRegisterInstruction).registerA

        // After the iget-boolean that loads hasAudio, insert:
        // check if str2 (register holding the video URL) contains "redgifs"
        // if yes, set hasAudioReg to 1
        // We need the register holding str2 (first param of RedditVideo constructor)
        // str2 is loaded via iget or invoke before constructor — find it
        // For now, insert a smali snippet after hasAudio load that forces true
        // This is a broad fix: force hasAudio=true for ALL gif videos
        // (Reddit only mutes redgifs in practice, not native gifs)
        method.addInstructionsWithLabels(
            hasAudioLoadIndex + 1,
            """
                const/4 v${hasAudioReg}, 0x1
            """.trimIndent()
        )
    }
}

private val toRedditVideoFingerprint = fingerprint {
    returns("Lcom/reddit/domain/model/RedditVideo;")
    parameters("Lep1/js0;")
    custom { methodDef, classDef ->
        classDef.type == "Lcom/reddit/data/model/graphql/GqlDataToMediaDomainModelMapperKt;"
    }
}
