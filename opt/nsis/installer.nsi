;
; Wezzle Installer
; By Cameron McKay (cam@couchware.ca)
;

;--------------------------------
; Settings

  !define APP_VENDOR "Couchware"
  !define APP_SHORT_NAME "Wezzle"
  !define APP_FULL_NAME "Wezzle"
  !define APP_VERSION "1.0-beta1"
  !define JAVA_REQUIRED "1.5.0"
  !define JAVA_INSTALLER "..\jre-installer\jre-6u17-windows-i586-s.exe"
  !define HKCU_REG_KEY "Software\${APP_VENDOR}\${APP_FULL_NAME}"
  !define HKLM_REG_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_FULL_NAME}"
  !define USER_DIR "$PROFILE\.Couchware\Wezzle"

;--------------------------------
; Include Modern UI
; Include LogicLib

  !include "MUI2.nsh"
  !include "LogicLib.nsh"

;--------------------------------
; General

  ; Name and file
  Name "${APP_FULL_NAME}"
  OutFile "${APP_SHORT_NAME}-${APP_VERSION}-setup.exe"

  ; Default installation folder
  InstallDir "$PROGRAMFILES\${APP_VENDOR}\${APP_FULL_NAME}" 

  ; Request application privileges for Windows Vista
  RequestExecutionLevel admin

;--------------------------------
; Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
; Pages  

  !define MUI_COMPONENTSPAGE_SMALLDESC

  !insertmacro MUI_PAGE_LICENSE "..\License.txt"
  !insertmacro MUI_PAGE_COMPONENTS  
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES

  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
; Languages

  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
; Installer Sections

Section "" SecUninstallPrevious

    ; Deletes user files.  This should only be used in test versions.
    Call DeleteUserFiles

    ; Runs uninstaller
    Call UninstallPrevious

SectionEnd

Section "Wezzle" SecWezzle
  
  ; Make it required.
  SectionIn RO

  SetOutPath "$INSTDIR"

  File /r ..\..\dist\*.*

  ; Store installation folder
  WriteRegStr HKLM "${HKLM_REG_KEY}" "InstallDir" $INSTDIR

  ; Setup Add/Remove information.
  WriteRegStr HKLM "${HKLM_REG_KEY}" "DisplayName" ${APP_FULL_NAME}
  WriteRegStr HKLM "${HKLM_REG_KEY}" "UninstallString" "$\"$INSTDIR\Uninstall.exe$\""
  WriteRegStr HKLM "${HKLM_REG_KEY}" "Publisher" ${APP_VENDOR}
  WriteRegStr HKLM "${HKLM_REG_KEY}" "DisplayVersion" ${APP_VERSION}
  WriteRegDWORD HKLM "${HKLM_REG_KEY}" "NoModify" 0x00000001
  WriteRegDWORD HKLM "${HKLM_REG_KEY}" "NoRepair" 0x00000001

  ; Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

Section "Java Runtime Environment" SecJava

  ; Make it required.
  SectionIn RO

  Push $0
  Push $1

  ; Detect Java.
  Call DetectJava
  Pop $0

  ; See if it was not found.
  ${If} $0 == ""

    ; Don't ask, just do.  Spolsky's teachings.
    ; MessageBox MB_OKCANCEL "The correct version of Java was not detected.$\r$\nSetup will now attempt to install the Java Runtime Environment." IDCANCEL JavaInstallerFailure

    DetailPrint "Installing the Java Runtime Environment..."   

    File /oname=$TEMP\java_installer.exe ${JAVA_INSTALLER}
    ExecWait '"$TEMP\java_installer.exe" /s /v\"/qn REBOOT=Suppress JAVAUPDATE=0 WEBSTARTICON=0\"' $0
    DetailPrint "Java Runtime Environment installation completed."
    Delete "$TEMP\java_installer.exe"

    ; Detect Java again, to see if it installed.
    Call DetectJava
    Pop $0

    ;JavaInstallerFailure:
    ;
    ;  ${If} $0 == ""
    ;    MessageBox MB_OK|MB_ICONEXCLAMATION "Setup was unable to install the Java Runtime Environment."
    ;  ${EndIf}

  ${EndIf}

SectionEnd

Section "Start Menu Shortcuts" SecStartMenuShortcuts

  CreateDirectory "$SMPROGRAMS\${APP_FULL_NAME}"  
  CreateShortCut "$SMPROGRAMS\${APP_FULL_NAME}\${APP_SHORT_NAME}.lnk" "$INSTDIR\${APP_SHORT_NAME}.exe" "" "$INSTDIR\${APP_SHORT_NAME}.exe" 0
  CreateShortCut "$SMPROGRAMS\${APP_FULL_NAME}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0

SectionEnd

;--------------------------------
; Functions

Function .onInit

    ; Get installation folder from registry if available
    ReadRegStr $R0 HKLM "${HKLM_REG_KEY}" "InstallDir"
    ReadRegStr $R1 HKCU "${HKCU_REG_KEY}" ""

    ${IfNot} $R0 == ""
        StrCpy $INSTDIR $R0
    ${ElseIfNot} $R1 == ""
        StrCpy $INSTDIR $R1
    ${EndIf}

FunctionEnd

Function DeleteUserFiles

    ; Delete the user directory.
    RMDir /r "${USER_DIR}"

FunctionEnd

Function UninstallPrevious

    ; Check for uninstaller.
    ReadRegStr $R0 HKLM "${HKLM_REG_KEY}" "InstallDir"
    ReadRegStr $R1 HKCU "${HKCU_REG_KEY}" ""

    ${If} $R0 == ""
    ${AndIf} $R1 == ""
        Goto Done
    ${EndIf}

    DetailPrint "Removing previous Wezzle installation."    

    ; Run the uninstaller.
    ExecWait '"$INSTDIR\Uninstall.exe /S"'

    Done:

FunctionEnd

Function DetectJava

  Push $0   ; This will be used for the target version
  Push $1   ; This will be used for the detected version string
  Push $2   ; This will be used for the detected Java home directory
  Push $3   ; This will be used for testing the major...
  Push $4   ; ...and minor version of Java.

  ; Set the target version.
  StrCpy $0 "${JAVA_REQUIRED}"

  ; Try finding a JRE.
  DetailPrint "Searching for compatible Java Runtime Environment..."
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"  

  ${If} $1 != ""
    DetailPrint "Found Java Runtime Environment $1."
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$1" "JavaHome"
    ;MessageBox MB_OK "JavaHome was $2."
    Goto GetJava
  ${EndIf}

  ; Try finding a JDK.
  DetailPrint "Searching for compatible Java Development Kit..."
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"

  ${If} $1 != ""
    DetailPrint "Found Java Development Kit $1."
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$1" "JavaHome"
    Goto GetJava
  ${EndIf}

  ; Could not find a usable Java.
  Goto JavaNotFound

  ; This label tries to find the actual Java exectuable.
  GetJava:

    DetailPrint "Verifying that the installed Java is the correct version..."

    IfFileExists "$2\bin\java.exe" 0 JavaNotFound

    ; Get major version number.
    ; Example: $1 = X.Y.Z, now $3 and $4 are X
    StrCpy $3 $0 1			; Get requested major version.
    StrCpy $4 $1 1			; Get found major version.
    ;MessageBox MB_OK "Compared $3 to $4."
    IntCmp $4 $3 0 JavaNotFound JavaFound

    StrCpy $3 $0 1 2        ; Get requested minor version.
    StrCpy $4 $1 1 2        ; Get found minor version.
    ;MessageBox MB_OK "Compared $3 to $4."
    IntCmp $4 $3 JavaFound JavaNotFound JavaFound

  ; If we got here, Java was found within parameters.
  JavaFound:

    DetailPrint "The detected version of Java is correct."
    Push "$2\bin\java.exe"
    Goto DetectJavaEnd

  ; If we got here, Java was not found or was too low.
  JavaNotFound:

    DetailPrint "The detected version of Java is incorrect."
    Push ""
    Goto DetectJavaEnd

  ; This label ends the function and restores the stack.
  DetectJavaEnd:

    ; Stack   => rv,r4,r3,r2,r1,r0
    Exch	; => r4,rv,r3,r2,r1,r0
    Pop $4	; => rv,r3,r2,r1,r0
    Exch	; => r3,rv,r2,r1,r0
    Pop $3	; => rv,r2,r1,r0
    Exch 	; => r2,rv,r1,r0
    Pop $2	; => rv,r1,r0
    Exch	; => r1,rv,r0
    Pop $1	; => rv,r0
    Exch	; => r0,rv
    Pop $0	; => rv

FunctionEnd

;--------------------------------
; Descriptions

  ; Language strings
  LangString DESC_SecWezzle ${LANG_ENGLISH} "Installs the Wezzle game files."
  LangString DESC_SecJava ${LANG_ENGLISH} "Installs the Java Runtime Environment if needed."
  LangString DESC_SecStartMenuShortcuts ${LANG_ENGLISH} "Create Start Menu shortcuts for Wezzle."

  ; Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecWezzle} $(DESC_SecWezzle)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecJava} $(DESC_SecJava)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecStartMenuShortcuts} $(DESC_SecStartMenuShortcuts)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
; Uninstaller Section

Section "Uninstall"

  ; ADD YOUR OWN FILES HERE...

  Delete "$INSTDIR\Uninstall.exe"

  ; Remove shortcuts.
  Delete "$SMPROGRAMS\${APP_SHORT_NAME}\*.*"
  RMDir "$SMPROGRAMS\${APP_SHORT_NAME}"

  RMDir /r "$INSTDIR"

  DeleteRegKey /ifempty HKCU "${HKCU_REG_KEY}"
  DeleteRegKey /ifempty HKLM "${HKLM_REG_KEY}"

SectionEnd