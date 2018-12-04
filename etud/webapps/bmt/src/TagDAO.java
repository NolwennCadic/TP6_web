import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides the data-base access object for tags.
 *
 * @author Jan Mikac, Sebastien Viardot
 */
public class TagDAO {
    /**
     * SQL query for user login
     */
    private static final String SQL_READ_TAGS = "select id,name from Tag where user_id=?";
    private static final String SQL_INSERT_TAG = "INSERT INTO Tag(`name`, `user_id`) VALUES (?, ?) ";
    private static final String SQL_UPDATE_TAG = "UPDATE Tag set name = ? WHERE id=?";
    private static final String SQL_DELETE_TAG = "DELETE FROM Tag WHERE id=?";
    private static final String SQL_ATTACHED_BOOKMARK = "INSERT INTO BOOKMARK_TAG VALUES(?, ?)";
    private static final String SQL_DETACHED_BOOKARK = "DELETE FROM BOOKMARK_TAG WHERE BOOKMARKS_ID = ? AND TAGS_ID = ?";

    /**
     * Provides the tags of a user.
     *
     * @param user a user
     * @return user tags
     * @throws SQLException if the DB connection fails
     */
    public static List<Tag> getTags(User user) throws SQLException {
        List<Tag> list = new ArrayList<Tag>();
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_READ_TAGS);
            stmt.setLong(1, user.getId());
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                long id = result.getLong(1);
                String name = result.getString(2);
                Tag tag = new Tag(id, name);
                list.add(tag);
            }
            return list;
        } finally {
            conn.close();
        }
    }

    //Récupère le tag qui a un nom donné
    public static Tag getTagByName(String name, User user) throws SQLException {
        List<Tag> list = getTags(user);
        // Itere sur les tags pour trouver celui avec le nom name
        for (Tag tag : list) {
            if (tag.getName().equals(name)) {
                return tag;
            }
        }
        return null;
    }

    //Récupère le tag qui a un ID donné
    public static Tag getTagById(Long id, User user) throws SQLException {
        List<Tag> list = getTags(user);
        // Itere sur les tags pour trouver celui avec le tag id
        for (Tag tag : list) {
            if (tag.getId() == id) return tag;
        }
        return null;
    }

    //Enregistre un tag sur la base de donnée
    public static void saveTag(Tag tag, User user) throws SQLException {
        //Ouvre la connection et
        Connection conn = DBConnection.getConnection();
        //insert le nouveau tag
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_TAG);
            stmt.setString(1, tag.getName());
            stmt.setLong(2, user.getId());
            stmt.executeUpdate();
        } finally {
            conn.close();
        }
    }

    //Modifie le name d'un tag
    public static void updateTag(Tag tag, String newTagName, User user) throws SQLException {
        // Ouvre la connection
        Connection conn = DBConnection.getConnection();
        //On modifie son nom
        tag.setName(newTagName);
        //modifie
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_TAG);
            stmt.setString(1, newTagName);
            stmt.setLong(2, tag.getId());
            stmt.executeUpdate();
        } finally {
            conn.close();
        }
        //http://localhost:8080/bmt/tata/tags/1?x-http-method=put&json={'id':1,'name':"toto"}
    }

    //Fonction pour supprimer un tag
    public static void deleteTag(Long id, User user) throws SQLException {
        //Ouvre la connection
        Connection conn = DBConnection.getConnection();
        //Supprime le tag
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_TAG);
            stmt.setLong(1, id);
            stmt.execute();
        } finally {
            conn.close();
        }

    }

    public static void attachedBookMark(Long bookmarkId, Long tagId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_ATTACHED_BOOKMARK);
            stmt.setLong(1, bookmarkId);
            stmt.setLong(2, tagId);
            stmt.execute();
        } finally {
            conn.close();
        }
    }

    public static void removeAttachmentBookmark(Long bookmarkId, Long tagId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_DETACHED_BOOKARK);
            stmt.setLong(1, bookmarkId);
            stmt.setLong(2, tagId);
            stmt.execute();
        } finally {
            conn.close();
        }
    }


}
