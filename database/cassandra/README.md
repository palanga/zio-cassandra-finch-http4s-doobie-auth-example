Troubleshooting
===============

* Cassandra doesn't work with java 14 (we are using java 11)
* Some jvm options doesn't work so we have to edit `/usr/local/etc/cassandra/jvm.options`
* Launch with `cassandra -f`
* Stop with `ps` to get the PID and then `kill <PID>`
