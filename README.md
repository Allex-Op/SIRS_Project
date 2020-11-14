# Medical Test Records

## Table of Contents

TODO

## About 

Project developed in the context of the curricular unit of SIRS (Segurança Informática em Redes e Sistemas).

## Environment Setup

1. TODO

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

Finally, issue the command "ansible-playbook deploy-app.yml", this will clone the repository, compile the project, create and populate the databases and start the application.

MySQL User:
admin:admin

Vagrant virtualboxes:
vagrant:vagrant


## TBD
TBD
