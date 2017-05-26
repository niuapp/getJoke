Set ws = CreateObject("Wscript.Shell")

do
'执行
dim returnstr
returnstr = ws.run ("cmd.exe /c java -jar C:\joke\joke.jar", 7, true)	',0  加上,0后台执行 7 最小化窗口运行


if returnstr<>0 then
msgbox returnstr
end if

'6秒后判断条件循环
Wscript.sleep 6000
Set fs = CreateObject("Scripting.FileSystemObject") 
Set file = fs.OpenTextFile("C:\joke\tvbs.txt", 1, false) 
dim str
str=file.readall
file.close 
set fs=nothing 
str = len(str)

'如果那个文件里的字符多于1个 就停止
if str>1 then
Wscript.quit
end if
loop