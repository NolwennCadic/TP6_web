import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cadicn on 11/28/18.
 */
public class BookmarkDAO {

    private static final String SQL_READ_BOOKMARKS = "select id, description, link, title from BOOKMARK where user_id=?";
    private static final String SQL_GET_BMS_TAG = "SELECT TAGS_ID  FROM BOOKMARK_TAG  where BOOKMARKs_ID = ?";
    private static final String SQL_INSERT_BOOKMARK = "INSERT INTO BOOKMARK(`description`, `link`, `title`, `user_id`)  VALUES  (?, ?, ?, ?)";
    private static final String SQL_GET_LIST_BM_OF_TAGS = "SELECT BOOKMARK.ID, BOOKMARK.TITLE, BOOKMARK.DESCRIPTION,"
            + "BOOKMARK.LINK  from TAG, BOOKMARK , BOOKMARK_TAG  where TAG.ID = BOOKMARK_TAG.TAGS_ID "
            + "AND BOOKMARK.ID = BOOKMARK_TAG.BOOKMARKS_ID AND BOOKMARK.USER_ID = ? AND TAG.ID = ?";
    private static final String SQL_MODIFY_BOOKMARK = "UPDATE BOOKMARK SET DESCRIPTION = ?, LINK=?, TITLE=?  WHERE ID = ?";
    private static final String SQL_DELETE_BOOKMARK = "DELETE FROM BOOKMARK WHERE ID = ?";


    /**
     * Renvoie la liste de tous les bookmarks
     * @param user
     * @return liste de tous les bookmarks présents dans la BD
     * @throws SQLException
     */
    public static List<Bookmark> getBookmarks(User user) throws SQLException {
        List<Bookmark> list = new ArrayList<Bookmark>();
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_READ_BOOKMARKS);
            stmt.setLong(1, user.getId());
            ResultSet result = stmt.executeQuery();
            while (result.next()){
                long id = result.getLong(1);
                String description = result.getString(2);
                String title = result.getString(4);
                String link = result.getString(3);
                Bookmark bookmark = new Bookmark(id, description, title, link);
                bookmark.setTags(BookmarkDAO.getTagsOfBookmark(bookmark.getId(), user));
                list.add(bookmark);
            }
            return list;
        } finally {
            conn.close();
        }
    }
    /**
     * Provides the tags of a given bookmark
     *
     * @param bookmarkId the id of the bookmark
     * @return the bookmark's tags
     * @throws SQLException
     */
    public static List<Tag> getTagsOfBookmark(long bookmarkId, User user) throws SQLException {
        List<Tag> list = new ArrayList<Tag>();
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_GET_BMS_TAG);
            stmt.setLong(1, bookmarkId);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                long id = result.getLong(1);
                Tag tag = TagDAO.getTagById(id, user);
                list.add(tag);
            }
            return list;
        } finally {
            conn.close();
        }
    }

    /**
     * Provides the list of the bookmarks related to the given tag with a certain id
     * @param tagId id of the tag
     * @param user user
     * @return
     * @throws SQLException
     */
    public static List<Bookmark> getListOfBookmarkFromTag(Long tagId, User user) throws SQLException {
        List<Bookmark> bmList = new ArrayList<Bookmark>();
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(SQL_GET_LIST_BM_OF_TAGS);
        stmt.setLong(1, user.getId());
        stmt.setLong(2, tagId);
        ResultSet result = stmt.executeQuery();
        try {
            while(result.next()) {
                long id = result.getLong(1);
                String description = result.getString(3);
                String title = result.getString(2);
                String link = result.getString(4);
                Bookmark bookmark = new Bookmark(id, description, title, link);
                bookmark.setTags(BookmarkDAO.getTagsOfBookmark(bookmark.getId(), user));
                bmList.add(bookmark);
            }
            return bmList;
        } finally {
            conn.close();
        }
    }

    /**
     * Recupere le tag avec l'id passé en paramètres
     * @param id id du tag
     * @param user utilisateur
     * @return le tag avec l'id id
     * @throws SQLException
     */
    public static Bookmark getBookmarkById(Long id, User user) throws SQLException {
        List<Bookmark> list = getBookmarks(user);
        // Itere sur les bookmarks pour trouver celui avec id
        for (Bookmark bookmark : list) {
            if (bookmark.getId() == id) return bookmark;
        }
        return null;
    }

    /**
     * Mets à jour un bookmark dans la base de données
     * @param bookmark
     * @param newTitle
     * @param newDescription
     * @param newLink
     * @param newTags
     * @param user
     * @throws SQLException
     */
    public static void updateBookmark(Bookmark bookmark, String newTitle, String newDescription,
                                      String newLink, List<Tag> newTags, User user) throws SQLException {
        // Ouvre la connection
        Connection conn = DBConnection.getConnection();
        //Modifie le tag dans la classe
        System.out.println("Description : " + newDescription);
        System.out.println("title " + newTitle);
        System.out.println("lien " + newLink);
        bookmark.setDescription(newDescription);
        bookmark.setTitle(newTitle);
        //Modifie le tag dans la base de donnée
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_MODIFY_BOOKMARK);
            stmt.setString(1, newDescription);
            stmt.setString(2, newLink);
            stmt.setString(3, newTitle);
            stmt.setLong(4, user.getId());
            stmt.executeUpdate();
        } finally {
            conn.close();
        }
        // Gère les nouveaux tags.
        // On supprime l'attachement des anciens, on attache les nouveaux
        for(Tag tag: bookmark.getTags()) {
            TagDAO.removeAttachmentBookmark( bookmark.getId(), TagDAO.getTagById(tag.getId(), user).getId());
        }
        // On attache les nouveaux, si le tag n'existe pas, on le cree avant de l'attacher
        for(Tag tag: newTags) {
            // si le tag existe, on l'attache
            if (TagDAO.getTagByName(tag.getName(), user) != null) {
                TagDAO.attachedBookMark(bookmark.getId(), TagDAO.getTagByName(tag.getName(), user).getId());
            }
            else {
                TagDAO.saveTag(tag, user);
                Tag createdTag = TagDAO.getTagByName(tag.getName(), user);
                TagDAO.attachedBookMark(bookmark.getId(), createdTag.getId());
            }
        }
        // On update la liste des tags du bookmark
        bookmark.setTags(newTags);
    }

    /**
     * Supprime un bookmark donné
     * @param id id du bookmark à supprimer
     * @param user
     * @throws SQLException
     */
    public static void deleteBookmark(Long id, User user) throws SQLException {
        //Ouvre la connection
        Connection conn = DBConnection.getConnection();
        //Supprime le tag
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_BOOKMARK);
            stmt.setLong(1, id);
            stmt.execute();
        } finally {
            conn.close();
        }
    }

    /**
     * REnvoie le bookmark avec le lien passe en parametre
     * @param link lien du bookmark qu'on cherche
     * @param user
     * @return bookmark
     * @throws SQLException
     */
    public static Bookmark getBookmarkByLink(String link, User user) throws SQLException {
        List<Bookmark> list = getBookmarks(user);
        // Itere sur les bookmarks pour trouver celui avec id
        for (Bookmark bookmark : list) {
            if (bookmark.getLink().equals(link)) {
                return bookmark;
            }
        }
        return null;
    }

    /**
     * Sauvegarde le marque page dans la BD
     * @param description
     * @param link
     * @param title
     * @param user
     * @throws SQLException
     */
    public static void saveBookmark(String description, String link, String title, User user) throws SQLException {
        //Ouvre la connection et
        Connection conn = DBConnection.getConnection();
        //insert le nouveau bookmark
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_BOOKMARK);
            stmt.setString(1, description);
            stmt.setString(2, link);
            stmt.setString(3, title);
            stmt.setLong(4, user.getId());
            stmt.executeUpdate();
        } finally {
            conn.close();
        }
    }
}
