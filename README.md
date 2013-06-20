#Salina SDK for Android 1.0 beta
### Salina Feedback Solution을 안드로이드 플랫폼에서 편리하게 사용할 수 있도록 도와주는 SDK입니다.

##Salina SDK의 특징
1. Library Project로 배포됩니다.
2. 사용자 피드백 전송을 위한 API를 제공합니다.
3. 시스템 피드백 전송을 위한 API를 제공합니다.
4. 제공되는 API는 사용이 편리하고 직관적인 인터페이스로 구성되어있습니다.
5. 사용자 피드백 전송 시 편의를 위해 FeedbackLabel이나 FeedbackActivity와 같은 위젯을 제공합니다.

- - -

##Getting Start Salina SDK
Salina SDK를 사용하기 위해서는 개발환경에서 SDK를 적용하고자 하는 프로젝트에 Salina SDK Project를 라이브러리 프로젝트로 추가합니다.

####1. Manifest 설정
Salina에서는 애플리케이션의 버전을 파악하기 위해 AndroidManifest.xml파일의 루트 엘리먼트인 manifest에 설정된 `android:versionName="<Version Name>"` 속성을 사용합니다.

IDE의 경우 애플리케이션을 생성하면 기본적으로 `android:versionName="1.0"`으로 설정되어 있습니다.

#####1.1. uses-permission 등록
Salina SDK는 아래와 같은 권한을 요구합니다.

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

######INTERNET Permission
수집된 사용자 피드백과 시스템 피드백을 서버로 전송하여 수집하기 위해서는 네트워크 권한이 필요합니다.
######SYSTEM_ALERT_WINDOW
위젯으로 제공되는 FeedbackLabelService를 사용하는 경우 레이아웃에 영향을 주지 않기 위해 피드백 레이블을 스크린의 최상위에 배치합니다. 이를 위해서는 최상위 윈도우를 다루기 위해 WindowManager 시스템 서비스를 사용하게 됩니다. 이때 SYSTEM_ALERT_WINDOW 권한이 필요합니다.
######ACCESS_NETWORK_STATE
이 권한은 현재 네트워크의 상태를 조회하기 위해 필요한 권한입니다. 네트워크 타입 조회 및, 네트워크 상태에 따른 전송 여부를 결정하기 위해서 이 권한을 사용합니다.

#####1.2. widget 사용을 위한 액티비티 및 서비스 등록
위젯은 io.salina.android.widget에 위치해 있습니다. 여기에 존재하는 액티비티와 서비스를 등록하여야 정상적으로 제공되는 위젯을 사용할 수 있습니다.

######피드백 레이블 서비스 등록
`<service android:name="io.salina.android.widget.FeedbackLabelService"/>`

######피드백 액티비티 등록
ActionBarSherlock의 테마를 적용할 수 있습니다.

```
<activity android:name="io.salina.android.widget.FeedbackActivity"
		  android:theme="@style/Theme.Sherlock.Light"/>
```


######보낸 피드백 확인을 위한 액티비티 등록
`<activity android:name="io.salina.android.www.ContentsActivity"/>`

####2. salina_app_info.xml 설정
이 파일을 통해서 애플리케이션의 기본 설정 및 스크린 정보를 불러와 SDK에서 사용하게 됩니다. 따라서 해당 파일이 없을 시 정상적인 동작을 하지 못하므로 유의해 주십시오.
salina_app_info.xml 파일은 Salina SDK 프로젝트의 /res/xml에 위치해 있으며, 같은 이름의 파일을 개발하고 계신 프로젝트의 /res/xml에 만드시면 해당 파일은 오버라이딩 되어 개발 프로젝트의 파일이 우선 적용됩니다.
#####Xml Scheme
```
<salina appId="<appId>">
	<screens>
		<screen name="<Screen Name>" [class="<Activity Class Path>"]>
			<fucntion>function name</function>
			<function>…</function>
			…
		</screen>
		<screen>
		…
		</screen>
	</screens>
</salina>
```
[Salina Web Service](http://www.sailna.io)에서 회원 가입을 한 후 앱을 등록하면 앱을 구분하기 위한 Application ID를 발급받아 `<salina>` 엘리먼트의 appId 속성의 값으로 설정해주십시오.

`screens`는 스크린 목록을 정의하는 엘리먼트입니다. 하위에 screen 태그를 추가하고, 다시 screen 엘리먼트는 function을 자식 엘리먼트를 갖습니다. 이 스크린과 기능 정의를 이용해서 카테고리화된 사용자 피드백을 얻을 수 있습니다.

