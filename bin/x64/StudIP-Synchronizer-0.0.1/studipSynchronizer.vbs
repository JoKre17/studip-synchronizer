scriptdir = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)
Dim WinScriptHost
Set WinScriptHost = CreateObject("WScript.Shell")
WinScriptHost.Run Chr(34) & scriptdir & "\StudIP Synchronizer.exe" & Chr(34), 0
Set WinScriptHost = Nothing