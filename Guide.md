## [Install the LAMP stack](https://help.ubuntu.com/community/ApacheMySQLPHP)

```sh
sudo apt-get install lamp-server^
```
### Troubleshooting Apache

If you get this error:

> apache2: Could not determine the server's fully qualified domain name, using 127.0.0.1 for ServerName

Create `fqdn.conf` and enable the new configuration file

```sh
echo "ServerName localhost" | sudo tee /etc/apache2/conf-available/fqdn.conf && sudo a2enconf fqdn
```

## Run File Uploaded Site

1. 复制 AndroidFileUpload 文件夹到 `/var/www/` 目录

2. 修改 `AndroidFileUpload/uploads` 文件夹权限，使上传的文件能保存到 uploads 文件夹中
    ```sh
    sudo chown -R www-data:www-data uploads
    ```

3. 修改 PHP 配置文件 `/etc/php5/apache2/php.ini` 中下列值，以接收 post 大于 2M 的文件

    > upload_max_filesize = 50M  
    post_max_size = 50M  
    max_input_time = 300  
    max_execution_time = 300

4. 重启 Apache 服务
    ```sh
    sudo service apache2 restart
    ```

## Run UDP Sender Server


```sh
javac UDPServer.java
java UDPServer
```