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
        // === HOOK 1: VideoMedia constructor (PRIMARY - handles RedGifs embeds) ===
        // VideoMedia(String embedHtml, String url, MediaDimensions, VideoAttribution)
        // p0=this, p1=embedHtml, p2=url, p3=dimensions, p4=attribution
        //
        // When the app creates a VideoMedia for a RedGifs embed, we intercept it:
        // 1. Call getRedGifsHdUrl(embedHtml, url) to get the direct HD mp4 URL
        // 2. Replace embedHtml (p1) with an HTML5 video tag
        // 3. Replace url (p2) with the direct mp4 URL
        val videoMediaMethod = VideoMediaConstructorFingerprint.method
        videoMediaMethod.addInstructions(
            0,
            """
                invoke-static {p1, p2}, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->getRedGifsHdUrl(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
                move-result-object p2
                
                if-eqz p2, :cond_skip_vm
                
                invoke-static {p2}, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->createVideoHtml(Ljava/lang/String;)Ljava/lang/String;
                move-result-object p1
                
                :cond_skip_vm
            """.trimIndent()
        )

        // === HOOK 2: RedditVideo constructor (FALLBACK - if Reddit proxies RedGifs as RedditVideo) ===
        // p5 is fallbackUrl. If it contains "redgifs", fetch the real URL.
        val redditVideoMethod = RedditVideoConstructorFingerprint.method
        redditVideoMethod.addInstructions(
            0,
            """
                invoke-static {p5}, Lapp/morphe/patches/reddit/misc/redgifsaudio/RedGifsHelper;->fetchAudioUrl(Ljava/lang/String;)Ljava/lang/String;
                move-result-object v0
                
                if-eqz v0, :cond_skip_rv
                
                move-object p1, v0
                move-object p3, v0
                move-object p5, v0
                move-object p8, v0
                const/4 p9, 0x0
                
                :cond_skip_rv
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
