currDir=$(echo $PWD)
hospitalDir="$currDir/apis/hospital"
hospitalSnapshot="$hospitalDir/target/hospital-0.0.1-SNAPSHOT.jar"
pdpDir="$currDir/apis/pdp"
pdpSnapshot="$pdpDir/target/pdp-0.0.1-SNAPSHOT.jar"
labDir="$currDir/apis/lab"
labSnapshot="$labDir/target/lab-0.0.1-SNAPSHOT.jar"
destDir="$currDir/vagrant/examples/snapshots"

cd $hospitalDir
mvn clean package
cp $hospitalSnapshot $destDir

cd $pdpDir
mvn clean package
cp $pdpSnapshot $destDir

cd $labDir
mvn clean package
cp $labSnapshot $destDir
