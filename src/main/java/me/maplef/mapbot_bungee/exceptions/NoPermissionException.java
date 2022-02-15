package me.maplef.mapbot_bungee.exceptions;

public class NoPermissionException extends Exception{
    public NoPermissionException(){
        super("你没有使用此命令的权限");
    }
}
