package obj.quickblox.sample.chat.java.utils;

import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collection;


public class CollectionsUtils {

    public static String makeStringFromUsersFullNames(ArrayList<QBUser> allUsers) {
        StringifyArrayList<String> usersNames = new StringifyArrayList<>();

        for (QBUser usr : allUsers) {
            if (usr.getFullName() != null) {
                usersNames.add(usr.getFullName());
            } else if (usr.getId() != null) {
                usersNames.add(usr.getFullName());
            }
        }
        return usersNames.getItemsAsString().replace(",", ", ");
    }

    public static ArrayList<Integer> getIdsSelectedOpponents(Collection<QBUser> selectedUsers) {
        ArrayList<Integer> opponentsIds = new ArrayList<>();
        if (!selectedUsers.isEmpty()) {
            for (QBUser qbUser : selectedUsers) {
                opponentsIds.add(qbUser.getId());
            }
        }

        return opponentsIds;
    }
}