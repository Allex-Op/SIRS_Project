#!/usr/bin/env bash

sudo apt-get update
sudo ln -sf /usr/share/zoneinfo/UTC /etc/localtime
# install ansible (http://docs.ansible.com/intro_installation.html)
sudo apt-get -y install software-properties-common
apt-add-repository -y ppa:ansible/ansible
sudo apt-get update
sudo apt-get -y install ansible

# prepare to setup SSH Trust
cp /home/vagrant/examples/mgmtkeys/id* .ssh/


# configure hosts file for the internal network defined by Vagrantfile
cat >> /etc/hosts <<EOL

# vagrant environment nodes
192.168.56.9  mgmt
192.168.56.11  hospital
192.168.56.12  pdp
192.168.57.11  lab1
EOL
