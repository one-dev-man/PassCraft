source private-data/private.config.sync.sh

SOURCE=.
DESTINATION="$(private.DESTINATION)"
SSH_KEY="$(private.SSH_KEY)"

RSYNC_ARGS="--delete --exclude-from excluded.sync.sh --exclude-from .gitignore"
#SSH_ARGS=""

# Defining a custom task :
#   The task can be called from 'sync.sh <taskname>' and it will execute the specified command.
#   If you want to execute a command into $DESTINATION, you can use the native function 'remote "<args>"'
#TASK_<taskname>=""

TASK_CLEAN='pwd ; remote "ls -A | xargs rm -rf"'

TASK_BUILD='remote "mvn package"'

TASK_CP2TESTSERV='remote "rm '"$(private.REMOTE_TEST_SERVER_DIRECTORY)"'/PassCraft.jar ; cp target/PassCraft.jar '"$(private.REMOTE_TEST_SERVER_DIRECTORY)"'"'

# 

TASK_BUILD_ROUTINE='sync.sh clean sync build cp2testserv'

# 

TASK_SCREEN_TEST_SERV='remote "screen -r mcsrv-test"'

# 

TASK_CLEAN_GIT='git checkout --orphan tmp ; git add . ; git commit -m "$(read -p "Commit : " __commit_name__ ; echo "$__commit_name__")" ; git branch -D main ; git branch -M main'

TASK_PUBLISH_GIT='git push -u -f origin main'

# 

ARTIFACT_ID="$(echo "$(<pom.xml)" | grep -m 1 -o -P "(?<=<artifactId>).*(?=</artifactId>)")"
VERSION="$(echo "$(<pom.xml)" | grep -m 1 -o -P "(?<=<version>).*(?=</version>)")"
PACKAGING="$(echo "$(<pom.xml)" | grep -m 1 -o -P "(?<=<packaging>).*(?=</packaging>)")"
COMPILED_ARCHIVE_NAME="$ARTIFACT_ID"'-'"$VERSION"'.'"$PACKAGING"

DOWNLOADS_DIRECTORY="$SOURCE/downloads"

if ! [ -d "$(eval echo $DOWNLOADS_DIRECTORY)" ]; then
    mkdir "$DOWNLOADS_DIRECTORY"
fi

TASK_DLDEST='read -p "Destination file to download : " dlfp ; scp -i '"'""$SSH_KEY""'"' "'"$DESTINATION"'/$dlfp" "'"$DOWNLOADS_DIRECTORY"'"'

TASK_DLDEST_BUILD='scp -i '"'""$SSH_KEY""'"' "'"$DESTINATION"'/target/'"$COMPILED_ARCHIVE_NAME"'" "'"$DOWNLOADS_DIRECTORY"'"'

TASK_CLEAN_DLDEST='$(cd "'"$DOWNLOADS_DIRECTORY"'" ; ls -A | xargs rm -rf)'