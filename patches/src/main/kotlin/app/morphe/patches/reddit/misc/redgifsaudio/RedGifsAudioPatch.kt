package app.morphe.patches.reddit.misc.redgifsaudio

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch

@Suppress("unused")
val redgifsAudioPatch = bytecodePatch(
    name = "RedGifs Audio Fix",
    description = "Enables audio playback for RedGifs videos embedded in Reddit posts."
) {
    extendWith("redgifs_extension.dex")
    compatibleWith("com.reddit.frontpage")

    execute {
        val method = RedditVideoConstructorFingerprint.method
        method.addInstructions(
            0,
            """
                invoke-static {p5}, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->fetchAudioUrl(Ljava/lang/String;)Ljava/lang/String;
                move-result-object v0
                
                if-eqz v0, :cond_skip_redgifs
                
                move-object p1, v0
                move-object p3, v0
                move-object p5, v0
                move-object p8, v0
                const/4 p9, 0x0
                
                :cond_skip_redgifs
            """.trimIndent()
        )

        // 2. Force ExtraTags.isGifPost() to return false globally
        ExtraTagsIsGifPostFingerprint.method.addInstructions(0, """
            const/4 p0, 0x0
            return p0
        """.trimIndent())

        // 3. Force corexdata Media.getHasAudio() to return true globally
        CorexDataMediaGetHasAudioFingerprint.method.addInstructions(0, """
            const/4 p0, 0x1
            return p0
        """.trimIndent())

        // 4. Force coreplatform Media.getHasAudio() to return true globally
        CorePlatformMediaGetHasAudioFingerprint.method.addInstructions(0, """
            const/4 p0, 0x1
            return p0
        """.trimIndent())
    }
}

object RedditVideoConstructorFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/domain/model/RedditVideo;",
    returnType = "V",
    name = "<init>",
    parameters = listOf(
        "Ljava/lang/String;", 
        "Lcom/reddit/domain/model/RedditVideoMp4Urls;", 
        "Ljava/lang/String;", 
        "I", 
        "Ljava/lang/String;", 
        "I", 
        "I", 
        "Ljava/lang/String;", 
        "Z", 
        "Ljava/lang/String;", 
        "Ljava/lang/String;", 
        "Ljava/lang/String;"
    )
)

object ExtraTagsIsGifPostFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/domain/model/ExtraTags;",
    returnType = "Z",
    name = "isGifPost",
    parameters = emptyList()
)

object CorexDataMediaGetHasAudioFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/corexdata/common/Media;",
    returnType = "Z",
    name = "getHasAudio",
    parameters = emptyList()
)

object CorePlatformMediaGetHasAudioFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/coreplatform/common/Media;",
    returnType = "Z",
    name = "getHasAudio",
    parameters = emptyList()
)
