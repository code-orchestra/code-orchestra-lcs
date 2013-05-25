; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "COLT"
#define MyAppVersion "1.0.4"
#define MyAppPublisher "Code Orchestra"
#define MyAppURL "http://www.codeorchestra.com/"
#define MyAppExeName "COLT.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{C3787386-CC06-482F-8D95-5762BC132E69}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
OutputDir=C:\Documents and Settings\JOHN\Desktop
OutputBaseFilename=setup
Compression=lzma
SolidCompression=yes
ChangesAssociations=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "C:\programs\COLT\COLT.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\programs\COLT\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Dirs]  
Name: "{app}"; Permissions: everyone-modify;

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Registry]
Root: HKCR; Subkey: ".colt"; ValueType: string; ValueName: ""; ValueData: "COLTProject"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "COLTProject"; ValueType: string; ValueName: ""; ValueData: "COLT Project File"; Flags: uninsdeletekey
Root: HKCR; Subkey: "COLTProject\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\COLT.EXE,0"
Root: HKCR; Subkey: "COLTProject\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\COLT.EXE"" ""--launcher.openFile"" ""%1"" ""-name"" ""Code Orchestra Livecoding Tool"""

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

