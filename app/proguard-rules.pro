# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class android.net.*.* { *; }
#카카오 로그인 프로가드 ↓
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

#네이버 로그인 프로가드 ↓
-keep public class com.nhn.android.naverlogin.** { public protected *; }

#카카오 맵 프로가드
-keep class net.daum.mf.map.n.** { *; }
-keep class net.daum.mf.map.api.MapView { *; }
-keep class net.daum.android.map.location.MapViewLocationManager { *; }
-keep class net.daum.mf.map.api.MapPolyline { *; }
-keep class net.daum.mf.map.api.MapPoint** { *; }
-keep class net.daum.** { *; }
-keep class android.opengl.** { *; }