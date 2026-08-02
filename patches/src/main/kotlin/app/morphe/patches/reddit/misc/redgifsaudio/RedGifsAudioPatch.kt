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
        // Still needed if Reddit falls back to WebView player (unlikely for main feed)
        val videoMediaMethod = VideoMediaConstructorFingerprint.method
        videoMediaMethod.addInstructions(
            0,
            """
                invoke-static {p1, p2}, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->processVideoMedia(Ljava/lang/String;Ljava/lang/String;)V
                
                sget-object p1, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->vmEmbedHtml:Ljava/lang/String;
                sget-object p2, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->vmUrl:Ljava/lang/String;
            """.trimIndent()
        )

        // === HOOK 2: LinkMedia constructor ===
        // Passes the native RedditVideo object (p1) and the original VideoMedia object (p3).
        // Our Java helper will use reflection to grab the original RedGifs URL from VideoMedia,
        // fetch the HD video, and overwrite the native RedditVideo object with the HD URL
        // AND set isGif=false so the native speaker icon appears!
        val linkMediaMethod = LinkMediaConstructorFingerprint.method
        linkMediaMethod.addInstructions(
            0,
            """
                invoke-static {p1, p3}, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->processLinkMedia(Ljava/lang/Object;Ljava/lang/Object;)V
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

object LinkMediaConstructorFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/domain/model/LinkMedia;",
    returnType = "V",
    name = "<init>",
    parameters = listOf(
        "Lcom/reddit/domain/model/RedditVideo;",
        "Lcom/reddit/domain/model/StillMedia;",
        "Lcom/reddit/domain/model/VideoMedia;"
    )
)
