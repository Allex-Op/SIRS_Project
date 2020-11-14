# Medical Test Records

## Table of Contents

- About
- Deployment Setup
- Secrets & PKI
- API Authentication
- Tests

## About 

Project developed in the context of the curricular unit of SIRS (Segurança Informática em Redes e Sistemas).

## Deployment Setup

To deploy and create the necessary infrastructure you require Vagrant (https://www.vagrantup.com/) installed on your computer. 
After installing vagrant navigate to the "vagrant" folder and issue the command "vagrant up".

The command will create 5 vm's, one management node, the hospital infrastructure nodes and the laboratory infrastructure nodes.
The management node will use ansible (https://www.ansible.com/) to deploy the necessary software to the correct nodes.

After completing the "vagrant up" process, proceed to issue "vagrant ssh mgmt" and access the management node.

From the mgmt node main directory navigate to the examples folder with "cd examples", 
and issue the "ansible-playbook ssh-addkey.yml --ask-pass" command, when prompted for a password type "vagrant".
This will create the necessary SSH Trust to avoid the "The authenticity of host 'lab... ' cannot be established ." prompt.

Then issue the command "ansible-playbook setup-infrastructure.yml", this will install all the required software (JDK, Git, NTP, MySQL...).

Finally, issue the command "ansible-playbook deploy-app.yml", this will clone the repository, compile the projects, populate the databases and start the applications.

MySQL User:
admin:admin

Vagrant virtualboxes:
vagrant:vagrant


## Secrets & PKI

In the "/vagrant/examples/certificates" it can be found all the cryptographic material to enable the HTTPS communications.
The first file to pay attention is the "myCA.key", this is the private key of the CA, its password is 'root'.

The second file is the "myCA.pem", this is the root certificate, again with password 'root'. This certificate will be installed in all the machines involved in this project to verify
the issued certificates during the creation of secure channels.

The 'hospital.key' is the private key associated with the hospital and 'hospital.crt' is the hospital certificate signed by the Certification Authority with the CA private key.
Same for 'lab1.key' and 'lab1.crt'.

The hospital cryptographic material (key and certificate) are only present in the hospital and the same for the lab.

## API Authentication

For the end users (staff, etc.) to use their respective entities they must first send a POST request to the '/login' endpoint with their credentials in the body.
If the credentials are correct the API will create and answer with a token which is valid for a year (it could be a short period and then use refresh tokens, but such is not the 
goal of the project). 

This token will be received in the body of the answer (check the respective API documentation for more info).

The client must send this token in the AUTHORIZATION header of all requests or they will be rejected with 401 UNAUTHENTICATED.

Also as previously explained in the project proposal there is no mechanism to revoke tokens in case the users lose them or they get compromised.

## Tests

To test the whole project you can run the script ... TBD