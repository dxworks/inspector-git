gitSecondFolder=$(pwd)
cd "$1" || exit
git log -p -M5% -c -U0 > ~/Documents/dx/testLog.log