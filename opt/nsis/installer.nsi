;
; Wezzle Win32 Installer Script
;
; This file creates a Win32 installer that will install the .exe version of
; Wezzle to the computer.  It will also check for an appropriate version of
; the JRE.  If it is not found, it will install the bundled JRE.
;

!define AppName "Wezzle"
!define AppVersion "1.0"
!define Vendor "Couchware Inc."
!define JRE_VERSION "1.5.0"
!define JRE_INSTALLER_PATH "../jre-installer"
!define JRE_INSTALLER "jre-6u13-windows-i586-p.exe"

!include "MUI.nsh"
!include "Sections.nsh"

Var InstallRuntime
Var JREPath

;-------------------------------------------------------------------------------
; Configuration
;-------------------------------------------------------------------------------

; General
Name "${AppName}"
OutFile "${AppName}-${AppVersion}-win32-setup.exe"

; Folder selection page
InstallDir "$PROGRAMFILES\${AppName}"

; Get install folder from registry if available
InstallDirRegKey HKLM "SOFTWARE\${Vendor}\${AppName}" ""

; Installation types
; Uncomment if you want Installation types
;InstType "full"	

;-------------------------------------------------------------------------------
; Pages
;-------------------------------------------------------------------------------

; The license page.
!insertmacro MUI_PAGE_LICENSE "..\..\docs\eula.txt"

; This page checks for JRE. It displays a dialog based on JRE.ini if it needs to install JRE
; Otherwise you won't see it.
Page custom CheckInstalledRuntime

; Define headers for the 'Java installed successfully' page.
!define MUI_INSTFILESPAGE_FINISHHEADER_TEXT "Java installation complete"
!define MUI_PAGE_HEADER_TEXT "Installing Java Runtime Environment"
!define MUI_PAGE_HEADER_SUBTEXT "Please wait while we install the Java Runtime Environemnt."
!define MUI_INSTFILESPAGE_FINISHHEADER_SUBTEXT "Java runtime installed successfully."
!insertmacro MUI_PAGE_INSTFILES
!define MUI_INSTFILESPAGE_FINISHHEADER_TEXT "Installation complete"
!define MUI_PAGE_HEADER_TEXT "Installing ${AppName} ${AppVersion}"
!define MUI_PAGE_HEADER_SUBTEXT "Please wait while ${AppName} is being installed."

; Uncomment the next line if you want optional components to be selectable.
;!insertmacro MUI_PAGE_COMPONENTS

!define MUI_PAGE_CUSTOMFUNCTION_PRE myPreInstfiles
!define MUI_PAGE_CUSTOMFUNCTION_LEAVE RestoreSections
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;-------------------------------------------------------------------------------
; Modern UI Configuration
;-------------------------------------------------------------------------------

!define MUI_ABORTWARNING

;-------------------------------------------------------------------------------
; Languages
;-------------------------------------------------------------------------------

!insertmacro MUI_LANGUAGE "English"

;-------------------------------------------------------------------------------
; Language Strings
;-------------------------------------------------------------------------------

; Description.
LangString DESC_SecAppFiles ${LANG_ENGLISH} "Application files copy"

; Header.
LangString TEXT_JRE_TITLE ${LANG_ENGLISH} "Java Runtime Environment"
LangString TEXT_JRE_SUBTITLE ${LANG_ENGLISH} "Installation"
LangString TEXT_PRODVER_TITLE ${LANG_ENGLISH} "Installed version of ${AppName}"
LangString TEXT_PRODVER_SUBTITLE ${LANG_ENGLISH} "Installation cancelled"

;-------------------------------------------------------------------------------
; Reserve Files
;-------------------------------------------------------------------------------

;Only useful for BZIP2 compression

ReserveFile "jre.ini"
!insertmacro MUI_RESERVEFILE_INSTALLOPTIONS

;-------------------------------------------------------------------------------
; Installer Sections
;-------------------------------------------------------------------------------

Section -InstallRuntime jre

  Push $0
  Push $1
  
  StrCmp $InstallRuntime "yes" InstallRuntime JREPathStorage
  DetailPrint "Starting the Java installation..."

InstallRuntime:

  File /oname=$TEMP\${JRE_INSTALLER} ${JRE_INSTALLER_PATH}\${JRE_INSTALLER}  
  DetailPrint "Launching Java installer..."
  ;ExecWait "$TEMP\${JRE_INSTALLER} /S" $0
  ; The silent install /S does not work for installing the JRE, 
  ; Sun has documentation on the parameters needed.
  ExecWait '"$TEMP\${JRE_INSTALLER}" /s /v\"/qn REBOOT=Suppress JAVAUPDATE=0 WEBSTARTICON=0\"' $0
  DetailPrint "Java installation complete."
  Delete "$TEMP\${JRE_INSTALLER}"
  StrCmp $0 "0" VerifyInstall 0
  Push "The Java installer has been unexpectedly interrupted."
  Goto ExitInstallRuntime

VerifyInstall:

  DetailPrint "Verifying the Java installation..."

  Push "${JRE_VERSION}"
  Call DetectRuntime

  ; DetectRuntime's return value.
  Pop $0	  
  StrCmp $0 "0" ExitInstallRuntime 0
  StrCmp $0 "-1" ExitInstallRuntime 0
  Goto JavaExeVerif
  Push "Java installation failed."
  Goto ExitInstallRuntime

JavaExeVerif:

  IfFileExists $0 JREPathStorage 0
  Push "Cannot find $0."
  Goto ExitInstallRuntime

JREPathStorage:

  ; MessageBox MB_OK "Path Storage"
  !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "UserDefinedSection" "JREPath" $1
  StrCpy $JREPath $0
  Goto End

ExitInstallRuntime:

  Pop $1
  MessageBox MB_OK "The setup is about to be interrupted for the following reason: $1."
  Pop $1 	; Restore $1
  Pop $0 	; Restore $0
  Abort

End:

  Pop $1	; Restore $1
  Pop $0	; Restore $0

SectionEnd

Section "Installation of ${AppName}" SecAppFiles

  SectionIn 1 RO ; Full install, cannot be unselected.
                 ; If you add more sections be sure to add them here as well.
  SetOutPath $INSTDIR
  ;File /r "stream\"

  ; If you need the path to JRE, you can either get it here for from $JREPath
  ;!insertmacro MUI_INSTALLOPTIONS_READ $0 "jre.ini" "UserDefinedSection" "JREPath"  

  ; Store installation folder.
  WriteRegStr HKLM "SOFTWARE\${Vendor}\${AppName}" "" $INSTDIR

  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${AppName}" "DisplayName" "${AppName}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${AppName}" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${AppName}" "NoModify" "1"
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${AppName}" "NoRepair" "1"

  ; Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

Section "Start menu shortcuts" SecCreateShortcut
  SectionIn 1	; Can be unselected
  CreateDirectory "$SMPROGRAMS\${AppName}"
  CreateShortCut "$SMPROGRAMS\${AppName}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  ;CreateShortCut "$SMPROGRAMS\${AppName}\${AppName}.lnk" "$INSTDIR\${AppName}.exe" "" "$INSTDIR\${AppName}.exe" 0
SectionEnd

;-------------------------------------------------------------------------------
; Descriptions
;-------------------------------------------------------------------------------

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
!insertmacro MUI_DESCRIPTION_TEXT ${SecAppFiles} $(DESC_SecAppFiles)
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;-------------------------------------------------------------------------------
; Installer Functions
;-------------------------------------------------------------------------------

Function .onInit

  ;Extract InstallOptions INI Files
  !insertmacro MUI_INSTALLOPTIONS_EXTRACT "jre.ini"
  Call SetupSections

FunctionEnd

Function myPreInstfiles

  Call RestoreSections
  SetAutoClose true

FunctionEnd

Function CheckInstalledRuntime
  
  Push "${JRE_VERSION}"
  Call DetectRuntime  

  ; Get return value from stack.
  Exch $0

  ; See if we found a suitable runtime.
  StrCmp $0 "0" NotFound
  StrCmp $0 "-1" NotFound
  Goto RuntimeInstalled

NotFound:
  
  Goto MustInstallRuntime

MustInstallRuntime:

  Exch $0	; $0 now has the install options page return value.
  ; Do something with return value here.
  Pop $0	; Restore $0
  StrCpy $InstallRuntime "yes"
  Return

RuntimeInstalled:
 
  StrCpy $InstallRuntime "no"
  !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "UserDefinedSection" "JREPath" $JREPATH
  Pop $0		; Restore $0
  Return

FunctionEnd

; Detects if a Java runtime exists.
; Version requested is on the stack.
; Return (on stack)
;   "0"   if not found
;   "-1"  if old version found
;   path  if current or new version found
Function DetectRuntime

  Exch $0 ; Get version requested.
          ; Now the previous value of $0 is on the stack, 
          ; and the asked for version of JDK is in $0.
  Push $1 ; $1 = Java version string (i.e. 1.5.0)
  Push $2 ; $2 = JavaHome
  Push $3 ; $3 and $4 are used for checking the major/minor version of java
  Push $4

  ; Look for the JRE.
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"

  ; If that fails, try finding the JDK.
  StrCmp $1 "" DetectJDK

  ; If we found the a current version, but no Java home, then look
  ; for the JDK.
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$1" "JavaHome"  
  StrCmp $2 "" DetectJDK

  Goto DetectVersion

DetectJDK:

  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"  
  StrCmp $1 "" NotFound

  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$1" "JavaHome"  
  StrCmp $2 "" NotFound

DetectVersion:

  ; $0 = Java version requested.
  ; $1 = Java version found.
  ; $2 = JavaHome
  ; 1.5.0 = Major.Minor.Bugfix

  IfFileExists "$2\bin\java.exe" 0 NotFound
  StrCpy $3 $0 1   ; Put requested major version number in $3.
  StrCpy $4 $1 1   ; Put found major version number in $4.

  ; Compare the version numbers.
  ; If the they are eqaul, look at the minor version number.
  IntCmp $4 $3 0 FoundOld Found
  StrCpy $3 $0 1 2   ; Put requested minor version number in $3.
  StrCpy $4 $1 1 2   ; Put found minor version number in $4.
  
  IntCmp $4 $3 Found FoundOld Found

NotFound:
  
  Push "0"
  Goto End

Found:

  Push "$2\bin\java.exe"
  Goto End

FoundOld:
  
  Push "-1"
  Goto End

End:

  ; Top of stack is return value, then r4,r3,r2,r1
  Exch   ; => r4,rv,r3,r2,r1,r0
  Pop $4 ; => rv,r3,r2,r1r,r0
  Exch   ; => r3,rv,r2,r1,r0
  Pop $3 ; => rv,r2,r1,r0
  Exch   ; => r2,rv,r1,r0
  Pop $2 ; => rv,r1,r0
  Exch   ; => r1,rv,r0
  Pop $1 ; => rv,r0
  Exch   ; => r0,rv
  Pop $0 ; => rv

FunctionEnd

Function RestoreSections

  !insertmacro UnselectSection ${jre}
  !insertmacro SelectSection ${SecAppFiles}
  !insertmacro SelectSection ${SecCreateShortcut}

FunctionEnd

Function SetupSections

  !insertmacro SelectSection ${jre}
  !insertmacro UnselectSection ${SecAppFiles}
  !insertmacro UnselectSection ${SecCreateShortcut}

FunctionEnd

;-------------------------------------------------------------------------------
; Uninstaller Section
;-------------------------------------------------------------------------------

Section "Uninstall"

  ; Remove registry keys.
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${AppName}"
  DeleteRegKey HKLM  "SOFTWARE\${Vendor}\${AppName}"

  ; Remove shortcuts, if any.
  Delete "$SMPROGRAMS\${AppName}\*.*"

  ; Remove files.
  RMDir /r "$INSTDIR"

SectionEnd