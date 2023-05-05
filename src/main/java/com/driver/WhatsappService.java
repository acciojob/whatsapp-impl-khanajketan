package com.driver;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class WhatsappService {
    WhatsappRepository whatsappRepository = new WhatsappRepository();
    public String createUser(String name, String mobile){
        if(whatsappRepository.getUserAndMobile().containsKey(mobile)){
            return "User already exists";
        }
        User u = new User(name, mobile);
        whatsappRepository.getUserMobile().add(mobile);
        whatsappRepository.getUserAndMobile().put(mobile, u);
        return "SUCCESS";
    }
    // The list contains at least 2 users where the first user is the admin. A group has exactly one admin.
    // If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
    // If there are 2+ users, the name of group should be "Group count". For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
    // Note that a personal chat is not considered a group and the count is not updated for personal chats.
    // If group is successfully created, return group.

    //For example: Consider userList1 = {Alex, Bob, Charlie}, userList2 = {Dan, Evan}, userList3 = {Felix, Graham, Hugh}.
    //If createGroup is called for these userLists in the same order, their group names would be "Group 1", "Evan", and "Group 2" respectively.
    public Group createGroup(List<User> users){
        if(users.size()< 2) return null;
        if(users.size() == 2){
            Group group = new Group(users.get(0).getName(), users.size());
            whatsappRepository.getGroupUserMap().put(group, users);
            whatsappRepository.getGroupMessageMap().put(group, new ArrayList<Message>());
            whatsappRepository.getAdminMap().put(group, users.get(0));
            return group;
        }
        int groupName = whatsappRepository.getCustomGroupCount();
        groupName++;
        Group group = new Group("Group"+groupName, users.size());
        whatsappRepository.getGroupUserMap().put(group, users);
        whatsappRepository.getGroupMessageMap().put(group, new ArrayList<Message>());
        whatsappRepository.getAdminMap().put(group, users.get(0));
        whatsappRepository.setCustomGroupCount(groupName);
        return group;

    }
    //Creating message
    public int createMessage(String content){
        int cnt = whatsappRepository.getMessageId();
        cnt++;
        Message message = new Message(cnt, content);
        whatsappRepository.setMessageId(cnt);
        return cnt;
    }
    public boolean findUserInGroup(User sender, Group group){
        List<User> list = whatsappRepository.getGroupUserMap().get(group);
        for(int i=0; i<list.size(); i++){
            if(list.get(i) == sender) return true;
        }
        return false;
    }
    public String changeAdmin(User approver, User user, Group group){
        if(!isGroupExist(group)){
            return "Group does not exist";
        }
        if(whatsappRepository.getAdminMap().get(group) != approver){
            return "Approver does not have rights";
        }
        if(!findUserInGroup(user,group)){
            return "User is not a participant";
        }
        whatsappRepository.getAdminMap().put(group, user);
        return "SUCCESS";
    }
    public boolean isGroupExist(Group group){
        if(whatsappRepository.getGroupUserMap().containsKey(group)) return true;
        return false;
    }
    public int sendMessage(Message message, User sender, Group group){
        if(!isGroupExist(group)){
            return -1;
        }else if(!findUserInGroup(sender,group)){
            return -2;

        }
        whatsappRepository.getGroupMessageMap().get(group).add(message);
        whatsappRepository.getSenderMap().put(message, sender);
        return  whatsappRepository.getGroupMessageMap().get(group).size();
    }
}