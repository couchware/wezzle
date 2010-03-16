<?php
/*
Template Name: Wezzle for Web
*/

$codebase = get_bloginfo("url") . "/../wezzle/applet";
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Wezzle for Web</title>
        <link rel="stylesheet" href="style.css" />
    </head>
    <body>
        <div id="body">
            <div id="window">

                <applet code="org.lwjgl.util.applet.AppletLoader"
                        codebase="<?php echo $codebase; ?>"
                        archive="lib/lwjgl/lwjgl_util_applet.jar, lib/lwjgl/lzma.jar, lib/audio_vorbisspi-1.0.2.jar, lib/audio_tritonus_share.jar, lib/audio_jorbis-0.0.15.jar, lib/audio_jogg-0.0.7.jar, lib/audio_basicplayer-3.0.jar" codebase="." width="800" height="600">

                    <!-- The following tags are mandatory -->

                    <!--
                    Name of Applet, will be used as name of directory it is
                    saved in, and will uniquely identify it in cache
                    -->
                    <param name="al_title" value="WezzleForWeb">

                    <!-- Main Applet Class -->
                    <param name="al_main" value="ca.couchware.wezzle2d.Launcher">

                    <!-- logo to paint while loading, will be centered -->
                    <param name="al_logo" value="appletlogo.png">

                    <!--
                    progressbar to paint while loading. Will be painted on
                    top of logo, width clipped to percentage done
                    -->
                    <param name="al_progressbar" value="appletprogress.gif">

                    <!-- List of Jars to add to classpath -->
                    <param name="al_jars" value="lib/lwjgl/lwjgl_applet.jar.pack.lzma, lib/lwjgl/lwjgl.jar.pack.lzma, lib/lwjgl/jinput.jar.pack.lzma, lib/lwjgl/lwjgl_util.jar.pack.lzma, Wezzle.jar, lib/slf4j-api-1.5.10.jar, lib/resources.jar, lib/logback-core-0.9.18.jar, lib/logback-classic-0.9.18.jar, lib/jdom.jar, lib/commons-logging-api.jar, lib/browserlauncher2-1.3.jar, lib/audio_vorbisspi-1.0.2.jar, lib/audio_tritonus_share.jar, lib/audio_jorbis-0.0.15.jar, lib/audio_jogg-0.0.7.jar, lib/audio_basicplayer-3.0.jar, lib/AppleJavaExtensions.jar, lib/AbsoluteLayout.jar">

                    <!-- signed windows natives jar in a jar -->
                    <param name="al_windows" value="lib/lwjgl/windows_natives.jar.lzma">

                    <!-- signed linux natives jar in a jar -->
                    <param name="al_linux" value="lib/lwjgl/linux_natives.jar.lzma">

                    <!-- signed mac osx natives jar in a jar -->
                    <param name="al_mac" value="lib/lwjgl/macosx_natives.jar.lzma">

                    <!-- Tags under here are optional -->

                    <!--
                    Version of Applet, important otherwise applet won't be cached,
                    version change will update applet, must be int or float
                    -->
                    <param name="al_version" value="1.3">

                    <!-- background color to paint with, defaults to white -->
                    <param name="al_bgcolor" value="000000">

                    <!-- foreground color to paint with, defaults to black -->
                    <param name="al_fgcolor" value="ffffff">

                    <!-- error color to paint with, defaults to red -->
                    <!-- <param name="al_errorcolor" value="ff0000"> -->

                    <!-- whether to run in debug mode -->
                    <param name="al_debug" value="false">

                    <!-- whether to prepend host to cache path - defaults to true -->
                    <!-- <param name="al_prepend_host" value="true"> -->

                    <!-- main applet specific params -->
                    <param name="separate_jvm" value="true">

                </applet>

            </div>
        </div>
    </body>
</html>

