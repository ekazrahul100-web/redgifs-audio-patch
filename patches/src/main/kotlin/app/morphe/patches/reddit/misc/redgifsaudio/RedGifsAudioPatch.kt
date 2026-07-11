package app.morphe.patches.reddit.misc.redgifsaudio

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

val redgifsAudioPatch = bytecodePatch(
    name = "RedGifs Audio Fix",
    description = "Enables audio playback for RedGifs videos embedded in Reddit posts."
) {
    compatibleWith("com.reddit.frontpage")

    execute {
        val method = GqlMediaMapperFingerprint.method
        val implementation = method.implementation ?: throw Exception("No implementation")
        val instructions = implementation.instructions

        instructions.forEachIndexed { index, instruction ->
            if (instruction.opcode == Opcode.CONST_4) {
                val oneReg = instruction as OneRegisterInstruction
                val next = instructions.getOrNull(index + 1)
                val nextStr = next?.toString() ?: ""
                if (nextStr.contains("hasAudio") || nextStr.contains("HAS_AUDIO")) {
                    method.addInstructions(
                        index,
                        "const/4 v${oneReg.registerA}, 0x1"
                    )
                }
            }
        }
    }
}

object GqlMediaMapperFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/data/model/graphql/GqlDataToMediaDomainModelMapperKt;",
    filters = listOf(
        string("isGif"),
        string("hasAudio")
    )
)
