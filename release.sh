summary() {
  echo ===========================================================================
  echo RELEASE PROCESS
  echo 1 mvn release:prepare release:perform -Plocalrelease
  echo 2 mvn -f target/checkout/demo/pom.xml android:manifest-update clean install -Pnovocation,release
  echo 3 cp target/checkout/demo/target/novocation-demo-zipped.apk ~/Desktop/novocation.apk
  echo 4 mvn android:manifest-update
  echo 5 git commit of changes to the android manifests for new development cycle
}

intro() {
  clear
  summary
  echo
  echo REQUIREMENTS
  echo localrelease profile
  echo release profile
  echo
  echo Do you want to continue?
  echo 
  read
  clear
}

break() {
  summary
  echo ===========================================================================
  echo
  echo $1 of 5 finished
  echo Do you want to continue?
  read
  clear
}

releasePrepareAndPerform() {
  mvn release:prepare release:perform -Plocalrelease
}

installTag() {
  mvn -f target/checkout/novocation/demo/pom.xml android:manifest-update clean install -Pnovocation,release
}

copyApkToDesktop() {
  cp target/checkout/novocation/demo/target/novocation-app-zipped.apk ~/Desktop/novocation.apk
}

androidManifestUpdate() {
  mvn android:manifest-update
}

committingAndroidManifestChanges() {
  git add **/AndroidManifest.xml
  git commit -m "[android-manifest] change versions to manifests for new development cycle"
  git push
}

end() {
  summary
  echo
  echo release finished
  echo ===========================================================================
}

intro
releasePrepareAndPerform
break 1
installTag
break 2
copyApkToDesktop
break 3
androidManifestUpdate
break 4
committingAndroidManifestChanges
end

