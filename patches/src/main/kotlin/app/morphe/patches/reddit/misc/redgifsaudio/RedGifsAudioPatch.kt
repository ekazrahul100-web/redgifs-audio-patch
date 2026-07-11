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
        // === HOOK 1: VideoMedia constructor ===
        // Calls our helper to process p1 and p2. 
        // Then we load the result from the helper's static fields.
        // This requires ZERO local registers (v0, etc) and is 100% verifier-safe.
        val videoMediaMethod = VideoMediaConstructorFingerprint.method
        videoMediaMethod.addInstructions(
            0,
            """
                invoke-static {p1, p2}, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->processVideoMedia(Ljava/lang/String;Ljava/lang/String;)V
                
                sget-object p1, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->vmEmbedHtml:Ljava/lang/String;
                sget-object p2, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->vmUrl:Ljava/lang/String;
            """.trimIndent()
        )

        // === HOOK 2: RedditVideo constructor ===
        // Calls our helper to process parameters. 
        // Then we load the result from the helper's static fields.
        val redditVideoMethod = RedditVideoConstructorFingerprint.method
        redditVideoMethod.addInstructions(
            0,
            """
                invoke-static {p1, p3, p5, p8, p9}, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->processRedditVideo(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V
                
                sget-object p1, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->rvP1:Ljava/lang/String;
                sget-object p3, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->rvP3:Ljava/lang/String;
                sget-object p5, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->rvP5:Ljava/lang/String;
                sget-object p8, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->rvP8:Ljava/lang/String;
                sget-boolean p9, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->rvP9:Z
            """.trimIndent()
        )
    }
}

object VideoMediaConstructorFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/domain/model/VideoMedia;",
    returnType = "V",
    name = "<init>",
    parameters = listOf(
        "Ljava/lang/String;",
        "Ljava/lang/String;",
        "Lcom/reddit/domain/model/MediaDimensions;",
        "Lcom/reddit/domain/model/VideoAttribution;"
    )
)

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
