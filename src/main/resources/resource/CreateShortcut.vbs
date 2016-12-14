Set oWS = WScript.CreateObject("WScript.Shell")
sLinkFile = oWS.ExpandEnvironmentStrings("%HOMEDRIVE%%HOMEPATH%\Desktop\{shortcutName}.lnk")
Set oLink = oWS.CreateShortcut(sLinkFile)
  oLink.TargetPath = "{pathToFolder}\{jarName}"
  oLink.IconLocation = "{pathToFolder}\{iconName}"
  oLink.WorkingDirectory = "{pathToFolder}"
oLink.Save