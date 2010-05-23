;
; Wezzle Installer
; By Cameron McKay (cam@couchware.ca)
;

;--------------------------------
; Settings

  !ifndef APP_VERSION
    !define APP_VERSION "unknown"
  !endif

  !define APP_VENDOR "Couchware"
  !define APP_SHORT_NAME "Wezzle"
  !define APP_FULL_NAME "Wezzle"  
  !define JAVA_REQUIRED "1.6.0"
  !define JAVA_INSTALLER "..\jre-installer\jre-6u17-windows-i586-s.exe"
  !define HKCU_REG_KEY "Software\${APP_VENDOR}\${APP_FULL_NAME}"
  !define HKLM_REG_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_FULL_NAME}"
  !define USER_DIR "$PROFILE\.couchware\Wezzle"
  
  !define SECRET_CODE "minsquibbion"
  !define HEXADECIMAL "0123456789abcdefABCDEF"
  ; R8 contains the serial number
  ; R9 contains the license key
    
  !define MUI_FINISHPAGE_RUN
  !define MUI_FINISHPAGE_RUN_TEXT "Run ${APP_SHORT_NAME}"  
  !define MUI_FINISHPAGE_RUN_FUNCTION ExecApplication
  
  ; This is for making the license key take uppercase only    
  !define UPPERCASE 0x8  
  
  !macro STYLE HWND STYLE
    
    ; Retrieve current style
    System::Call 'user32::GetWindowLongA(i ${HWND},i -16) i .r1'
    
    ; Append provided style
    IntOp $1 $1 | ${STYLE}
    
    ; Apply new style
    System::Call 'user32::SetWindowLongA(i ${HWND},i -16,i r1) n'
    
  !macroend  
  
  ; This is the workaround so we can have a pretty uninstaller name
  ; when the user manually runs the uninstaller.
  
  ; This is used by the uninstaller to communicate whether or not
  ; we've open a new process yet to mask the AU_.exe name.
  !define UNINSTALLER_INI "uac.ini"
  
  ; This is the pretty uninstaller name we'll use.
  !define UNINSTALLER_NAME "Uninstall"

;--------------------------------
; Include InstallOptions
; Include Modern UI
; Include LogicLib

  !include "InstallOptions.nsh"
  !include "MUI2.nsh"
  !include "LogicLib.nsh"

;--------------------------------
; General

  ; Name and file
  Name "${APP_FULL_NAME}"
  !ifdef TRIAL
    OutFile "${APP_SHORT_NAME}-${APP_VERSION}-dl-setup.exe"
  !else
    OutFile "${APP_SHORT_NAME}-${APP_VERSION}-setup.exe"
  !endif

  ; Default installation folder
  InstallDir "$PROGRAMFILES\${APP_VENDOR}\${APP_FULL_NAME}" 

  ; Request user privileges so we can use UAC plugin
  RequestExecutionLevel user 

;--------------------------------
; Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
; Pages  

  !define MUI_COMPONENTSPAGE_SMALLDESC

  !insertmacro MUI_PAGE_LICENSE "..\..\src\resources\license.txt"
  Page custom CreateLicensePage ValidateLicensePage
  !insertmacro MUI_PAGE_COMPONENTS  
  !insertmacro MUI_PAGE_DIRECTORY  
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH  

  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
; Languages

  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
; Installer Sections

Section "" SecUninstallPrevious

    ; Deletes user files.  This should only be used in test versions.
    ;Call DeleteUserFiles

    ; Runs uninstaller
    Call UninstallPrevious

SectionEnd

Section "Wezzle" SecWezzle
  
  ; Make it required.
  SectionIn RO

  ;MessageBox MB_OK|MB_ICONEXCLAMATION 'Serial number is: $R8'
  ;MessageBox MB_OK|MB_ICONEXCLAMATION 'License key is: $R9'

  SetOutPath "$INSTDIR"

  File /r ..\..\dist\*.*

  ; Write license information with user privileges.
  ; Do not write license if it already exists.
  !ifndef TRIAL
    IfFileExists "${USER_DIR}\license.xml" +3 0
    GetFunctionAddress $0 WriteLicense
    UAC::ExecCodeSegment $0
  !endif

  ; Store installation folder.
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

    GetFunctionAddress $0 CreateStartMenuShortcuts
    UAC::ExecCodeSegment $0     

SectionEnd

Section "Desktop Shortcut" SecDesktopShortcut

    GetFunctionAddress $0 CreateDesktopShortcut
    UAC::ExecCodeSegment $0

SectionEnd

;--------------------------------
; Init

Function .onInit
    
    UAC_Elevate:
        UAC::RunElevated 
        StrCmp 1223 $0 UAC_ElevationAborted ; UAC dialog aborted by user?
        StrCmp 0 $0 0 UAC_Err               ; Error?
        StrCmp 1 $1 0 UAC_Success           ; Are we the real deal or just the wrapper?
        Quit
 
    UAC_Err:
        MessageBox mb_iconstop "Setup was unable to elevate access (error $0)."
        Abort
     
    UAC_ElevationAborted:
        ; Elevation was aborted, run as normal?
        MessageBox mb_iconstop "Setup requires Administrator access to install."
        Abort
     
    UAC_Success:
        StrCmp 1 $3 +4                     ; Admin?
        StrCmp 3 $1 0 UAC_ElevationAborted ; Try again?
        MessageBox mb_iconstop "Setup requires Administrator access to install."
        goto UAC_Elevate     

    ; Get installation folder from registry if available
    ReadRegStr $R0 HKLM "${HKLM_REG_KEY}" "InstallDir"
    ReadRegStr $R1 HKCU "${HKCU_REG_KEY}" ""

    ${IfNot} $R0 == ""
        StrCpy $INSTDIR $R0
    ${ElseIfNot} $R1 == ""
        StrCpy $INSTDIR $R1
    ${EndIf}
    
    ; Extract the INI file for the license page.
    !insertmacro INSTALLOPTIONS_EXTRACT "license.ini"           

FunctionEnd

Function .OnInstFailed
    UAC::Unload ; Must call unload DLL.
FunctionEnd
 
Function .OnInstSuccess
    UAC::Unload ; Must call unload DLL.
FunctionEnd

;--------------------------------
; un.Init

Function un.onInit
    
    ReadIniStr $0 "$EXEDIR\${UNINSTALLER_INI}" UAC first
    ${If} $0 <> 1
        InitPluginsDir
        WriteIniStr "$PluginsDir\${UNINSTALLER_INI}" UAC first 1
        CopyFiles /SILENT "$EXEPATH" "$PluginsDir\${UNINSTALLER_NAME}.exe"
        ExecWait '"$PluginsDir\${UNINSTALLER_NAME}.exe" _?=$INSTDIR' $0
        SetErrorLevel $0
        Quit
    ${EndIf}
    
    UAC_Elevate:
        UAC::RunElevated 
        StrCmp 1223 $0 UAC_ElevationAborted ; UAC dialog aborted by user?
        StrCmp 0 $0 0 UAC_Err               ; Error?
        StrCmp 1 $1 0 UAC_Success           ; Are we the real deal or just the wrapper?
        Quit
 
    UAC_Err:
        MessageBox mb_iconstop "Uninstaller was unable to elevate access (error $0)."
        Abort
     
    UAC_ElevationAborted:
        ; Elevation was aborted, run as normal?
        MessageBox mb_iconstop "Uninstaller requires Administrator access to uninstall."
        Abort
     
    UAC_Success:
        StrCmp 1 $3 +4                     ; Admin?
        StrCmp 3 $1 0 UAC_ElevationAborted ; Try again?
        MessageBox mb_iconstop "Uninstaller requires Administrator access to uninstall."
        goto UAC_Elevate         

FunctionEnd

Function un.OnUnInstFailed
    UAC::Unload ; Must call unload DLL.
FunctionEnd
 
Function un.OnUnInstSuccess
    UAC::Unload ; Must call unload DLL.
FunctionEnd

;--------------------------------
; License

LangString PAGE_TITLE ${LANG_ENGLISH} "License Information"
LangString PAGE_SUBTITLE ${LANG_ENGLISH} "Please enter your license information."

Function CreateLicensePage

   ; If the license XML file already exists,
   ; then skip the license page.   
   IfFileExists "${USER_DIR}\license.xml" 0 +2
   Abort

   ; If we're in trial mode, skip this license screen, as the trial
   ; provides it.
   !ifdef TRIAL
     Abort
   !endif

   !insertmacro MUI_HEADER_TEXT $(PAGE_TITLE) $(PAGE_SUBTITLE)     
   
   InstallOptions::initDialog /NOUNLOAD "$PLUGINSDIR\license.ini"
   Pop $R0
   
   GetDlgItem $R1 $R0 1201 ; 1200 + Field number - 1
   !insertmacro STYLE $R1 ${UPPERCASE}
   
   GetDlgItem $R1 $R0 1203 ; 1200 + Field number - 1
   !insertmacro STYLE $R1 ${UPPERCASE}
   
   GetDlgItem $R1 $R0 1204 ; 1200 + Field number - 1
   !insertmacro STYLE $R1 ${UPPERCASE}
   
   GetDlgItem $R1 $R0 1205 ; 1200 + Field number - 1
   !insertmacro STYLE $R1 ${UPPERCASE}
   
   GetDlgItem $R1 $R0 1206 ; 1200 + Field number - 1
   !insertmacro STYLE $R1 ${UPPERCASE}
   
   InstallOptions::show
   Pop $R0            
      
   ; Read the registration name into R8.
   ReadINIStr $R8 "$PLUGINSDIR\license.ini" "Field 2" "State"  
   
   ; Read the registration code into R9.
   Call GetLicenseKey
   Pop $R9    

FunctionEnd

Function ValidateLicensePage
   
   Push $R0
   Push $R1
   Push $R2
             
   ; Field 1 is the serial number.
   ReadINIStr $R2 "$PLUGINSDIR\license.ini" "Field 2" "State"  
            
   ; Make sure there are no non-hex characters.     
   Push ${HEXADECIMAL}
   Push $R2 
   Call StrCSpnReverse
   Pop $R0   
   StrCmp $R0 "" +3   
   MessageBox MB_OK|MB_ICONEXCLAMATION 'Serial number contains an invalid character "$R0".   '
   Abort  
                 
   ; Read all the code blocks into one string.   
   Call GetLicenseKey
   Pop $R1
   
   ; Make sure there are no non-hex characters.
   Push ${HEXADECIMAL}
   Push $R1 
   Call StrCSpnReverse
   Pop $R0   
   StrCmp $R0 "" +3   
   MessageBox MB_OK|MB_ICONEXCLAMATION 'License key contains an invalid character "$R0".   '
   Abort               
   
   ; Make sure the registration code is correct for the given name.
   md5dll::GetMD5String "$R2${SECRET_CODE}"   
   Pop $R0
   StrCmp $R0 $R1 +3
   MessageBox MB_OK|MB_ICONEXCLAMATION 'Serial number or license key is incorrect.   '
   Abort               
   
   Pop $R2
   Pop $R1    
   Pop $R0

FunctionEnd

Function GetLicenseKey

   Push $R0 ; Code block
   Push $R1 ; Code

   StrCpy $R1 ""

   ; Field 4 is the 1st code block
   ; Field 5 is the 2nd code block
   ; ...
   ; Field 7 is the 4th code block      

   ReadINIStr $R0 "$PLUGINSDIR\license.ini" "Field 4" "State"
   StrCpy $R1 "$R1$R0"         
   
   ReadINIStr $R0 "$PLUGINSDIR\license.ini" "Field 5" "State"
   StrCpy $R1 "$R1$R0"
   
   ReadINIStr $R0 "$PLUGINSDIR\license.ini" "Field 6" "State"
   StrCpy $R1 "$R1$R0"
   
   ReadINIStr $R0 "$PLUGINSDIR\license.ini" "Field 7" "State"
   StrCpy $R1 "$R1$R0"   
   
   ; Push the full code onto the stack
   Push $R1
      
   ; Stack   => rv,r1,r0
   Exch    ; => r1,rv,r0
   Pop $R1 ; => rv,r0
   Exch    ; => r0,rv
   Pop $R0 ; => rv

FunctionEnd

;--------------------------------
; Uninstall Previous

;Function DeleteUserFiles
;
;    ; Delete the user directory.
;    RMDir /r "${USER_DIR}"
;
;FunctionEnd

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

;--------------------------------
; Java Detection

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

    DetailPrint "The detected version of Java is compatible."
    Push "$2\bin\java.exe"
    Goto DetectJavaEnd

  ; If we got here, Java was not found or was too low.
  JavaNotFound:

    DetailPrint "The detected version of Java is incompatible."
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
; Write License

Function WriteLicense

    CreateDirectory "${USER_DIR}"
    
    ; Write license information.
    ; We write to the game-settings.xml instead of
    ; the user-settings.xml so we don't clobber any
    ; existing user settings like high scores.
    ; Upon the first running of the game, Wezzle will
    ; automatically move the registration information
    ; into the user-settings.xml file.
    Delete "${USER_DIR}\license.xml"
  
    Var /GLOBAL ln0
    Var /GLOBAL ln1
    Var /GLOBAL ln2
    Var /GLOBAL ln3
    Var /GLOBAL ln4
    Var /GLOBAL ln5
  
    StrCpy $ln0 `<?xml version="1.0" encoding="UTF-8"?>$\r$\n`
    StrCpy $ln1 `<settings>$\r$\n`
    StrCpy $ln2 `  <entry name="User.Serial.Number">$R8</entry>$\r$\n`
    StrCpy $ln3 `  <entry name="User.License.Key">$R9</entry>$\r$\n`
    StrCpy $ln4 `  <entry name="User.Agreement.Accepted">true</entry>$\r$\n`
    StrCpy $ln5 `</settings>$\r$\n`
  
    Push "$ln0$ln1$ln2$ln3$ln4$ln5"
    Push "${USER_DIR}\license.xml"
    Call WriteToFile

FunctionEnd

;--------------------------------
; Create Shortcuts
 
Function CreateStartMenuShortcuts

    CreateDirectory "$SMPROGRAMS\${APP_FULL_NAME}"  
    CreateShortCut "$SMPROGRAMS\${APP_FULL_NAME}\${APP_SHORT_NAME}.lnk" "$INSTDIR\${APP_SHORT_NAME}.exe" "" "$INSTDIR\${APP_SHORT_NAME}.exe" 0
    CreateShortCut "$SMPROGRAMS\${APP_FULL_NAME}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0

FunctionEnd

Function CreateDesktopShortcut

    CreateShortCut "$DESKTOP\${APP_SHORT_NAME}.lnk" "$INSTDIR\${APP_SHORT_NAME}.exe" "" "$INSTDIR\${APP_SHORT_NAME}.exe" 0

FunctionEnd

;--------------------------------
; Execute Application with User Access

Function ExecApplication
    UAC::Exec '' '"$INSTDIR\${APP_SHORT_NAME}.exe"' '' '$INSTDIR'
FunctionEnd

;--------------------------------
; General Purpose Functions

; This function checks a string for invalid characters not in a list.
; @param1 string to check
; @param2 string of allowed characters
Function StrCSpnReverse

   Exch $R0 ; string to check
   Exch
   Exch $R1 ; string of characters
   Push $R2 ; current char
   Push $R3 ; current char
   Push $R4 ; char loop
   Push $R5 ; char loop
 
   StrCpy $R4 -1
 
   NextCharCheck:
   
   StrCpy $R2 $R0 1 $R4
   IntOp $R4 $R4 - 1
   StrCmp $R2 "" StrOK 
   StrCpy $R5 -1
 
   NextChar:
   
   StrCpy $R3 $R1 1 $R5
   IntOp $R5 $R5 - 1
   StrCmp $R3 "" +2
   StrCmp $R3 $R2 NextCharCheck NextChar
   StrCpy $R0 $R2
   Goto Done
 
   StrOK:
   
   StrCpy $R0 ""
 
   Done:
 
   Pop $R5
   Pop $R4
   Pop $R3
   Pop $R2
   Pop $R1
   Exch $R0
   
FunctionEnd

; Writes a string to a file.
; @param1 The text to write to the file.
; @param2 The file to write to.
Function WriteToFile

   Exch $0 ; File to write to
   Exch
   Exch $1 ; Text to write
 
   FileOpen $0 $0 a
      FileSeek $0 0 END ; Go to the end
      FileWrite $0 $1   ; Write to the file
   FileClose $0
 
   Pop $1
   Pop $0
   
FunctionEnd

;--------------------------------
; Descriptions

  ; Language strings
  LangString DESC_SecWezzle ${LANG_ENGLISH} "Installs the Wezzle game files."
  LangString DESC_SecJava ${LANG_ENGLISH} "Installs the Java Runtime Environment if needed."
  LangString DESC_SecStartMenuShortcuts ${LANG_ENGLISH} "Create Start Menu shortcuts for Wezzle."
  LangString DESC_SecDesktopShortcut ${LANG_ENGLISH} "Create Desktop shortcut for Wezzle."

  ; Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecWezzle} $(DESC_SecWezzle)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecJava} $(DESC_SecJava)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecStartMenuShortcuts} $(DESC_SecStartMenuShortcuts)
    !insertmacro MUI_DESCRIPTION_TEXT ${SecDesktopShortcut} $(DESC_SecDesktopShortcut)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
; Uninstaller Section

Section "Uninstall"

  Delete "$INSTDIR\Uninstall.exe"

  ; Remove shortcuts.
  Delete "$DESKTOP\${APP_SHORT_NAME}.lnk"
  Delete "$SMPROGRAMS\${APP_SHORT_NAME}\*.*"
  RMDir "$SMPROGRAMS\${APP_SHORT_NAME}"

  RMDir /r "$INSTDIR"

  DeleteRegKey /ifempty HKCU "${HKCU_REG_KEY}"
  DeleteRegKey /ifempty HKLM "${HKLM_REG_KEY}"

SectionEnd