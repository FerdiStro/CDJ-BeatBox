#Needed in Linux Setups.
sudo setcap 'cap_net_bind_service,cap_net_admin,cap_net_raw+eip' $(readlink -f $(which java))