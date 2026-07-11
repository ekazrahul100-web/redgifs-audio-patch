package app.morphe.patches.reddit.misc.redgifsaudio

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.fingerprint.MethodFingerprint
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "RedGifs Audio Fix",
    description = "Enables audio playback for RedGifs videos embedded in Reddit posts.",
    compatiblePackages = [CompatiblePackage("com.reddit.frontpage")]
)
object RedGifsAudioPatch : BytecodePatch(
    setOf(GqlMediaMapperFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        GqlMediaMapperFingerprint.result?.let { result ->
            val method = result.mutableMethod
            val implementation = method.implementation!!
            val instructions = implementation.instructions

            instructions.forEachIndexed { index, instruction ->
                if (instruction.opcode == Opcode.CONST_4) {
                    val oneReg = instruction as OneRegisterInstruction
                    val next = instructions.getOrNull(index + 1)
                    val nextStr = next?.toString() ?: ""
                    if (nextStr.contains("hasAudio") || nextStr.contains("HAS_AUDIO")) {
                        method.addInstructionsWithLabels(
                            index,
                            "const/4 v${oneReg.registerA}, 0x1"
                        )
                    }
                }
            }
        } ?: throw Exception("GqlMediaMapperFingerprint not found")
    }
}

object GqlMediaMapperFingerprint : MethodFingerprint(
    strings = listOf("isGif", "hasAudio"),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass == "Lcom/reddit/data/model/graphql/GqlDataToMediaDomainModelMapperKt;"
    }
)
