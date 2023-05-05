package com.driver;

@Service
public class WhatsappService {
    WhatsappRepository whatsappRepository = new WhatsappRepository();
    public String createUser(String name, String mobile){
        if(whatsappRepository.userAndMobile.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        User u = new User(name, mobile);
        whatsappRepository.userMobile.add(mobile);
        whatsappRepository.userAndMobile.put(mobile, u);
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
        if(users.size() == 2){
            Group group = new Group(users.get(0), users.size());
            whatsappRepository.groupUserMap(group, users);
            whatsappRepository.groupMessageMap(group, new ArrayList<Message>);
            whatsappRepository.adminMap(group, users.get(0));
            int cnt = whatsappRepository.customGroupCount;
            cnt++;
            whatsappRepository.customGroupCount = cnt;
            return group;
        }
    }
    //Creating message
    public int createMessage(String content){
        int cnt = whatsappRepository.messageId;
        cnt++;
        Message message = new Message(cnt, content, System.currentTimeMillis());
        whatsappRepository.messageId = cnt;
        return cnt;
    }
    public boolean findUserInGroup(User sender, Group group){
        List<User> list = whatsappRepository.groupUserMap.get(group);
        for(int i=0; i<list.size(); i++){
            if(list.get(i) == sender) return true;
        }
        return false;
    }
    public String changeAdmin(User approver, User user, Group group){
        if(!isGroupExist(group)){
            throw new RuntimeException("Group does not exist");
        }
        if(whatsappRepository.adminMap.get(group) != approver){
            throw new RuntimeException("Approver does not have rights");
        }
        if(!findUserInGroup(sender,group){
            throw new RuntimeException("User is not a participant");
        }
        whatsappRepository.adminMap.put(group, user);
    }
    public boolean isGroupExist(Group group){
        if(whatsappRepository.groupUserMap.containsKey(group)) return true;
        return false;
    }
    public int sendMessage(Message message, User sender, Group group){
        if(!isGroupExist(group)){
            throw new RuntimeException("Group does not exist");
        }else if(!findUserInGroup(sender,group)){
            throw new RuntimeException("You are not allowed to send message");

        }
        whatsappRepository.groupMessageMap.get(group).add(message);
        whatsappRepository.senderMap.put(message, sender);
        return  whatsappRepository.groupMessageMap.get(group).size();
    }
}