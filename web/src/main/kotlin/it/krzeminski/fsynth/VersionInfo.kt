package it.krzeminski.fsynth

import it.krzeminski.fsynth.generated.gitInfo
import it.krzeminski.gitinfo.types.GitInfo
import kotlinext.js.js
import kotlinx.html.style
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RProps
import react.RState
import react.dom.a
import react.dom.div
import react.dom.span
import kotlin.js.Date

class VersionInfo : RComponent<VersionInfoProps, RState>() {
    override fun RBuilder.render() {
        div {
            attrs.style = js {
                color = "#555"
                fontSize = "0.8rem"
                textAlign = "center"
                marginTop = "10px"
            }
            +"Version "
            span {
                attrs.style = js {
                    fontFamily = "monospace"
                }
                a("https://github.com/krzema12/fsynth/commit/${gitInfo.latestCommit.sha1}") {
                    +gitInfo.latestCommit.sha1.substring(0, 8)
                }
            }
            +" from ${Date(gitInfo.latestCommit.timeUnixTimestamp*1000).toUTCString()}"
        }
    }
}

external interface VersionInfoProps : RProps {
    var gitInfo: GitInfo
}

fun RBuilder.versionInfo(handler: RHandler<VersionInfoProps>) = child(VersionInfo::class) {
    handler()
}
