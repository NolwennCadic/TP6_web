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

    private static final String SQL_GET_LIST_BM_OF_TAGS = "SELECT BOOKMARK.ID, BOOKMARK.TITLE, BOOKMARK.DESCRIPTION,"
            + "BOOKMARK.LINK  from TAG, BOOKMARK , BOOKMARK_TAG  where TAG.ID = BOOKMARK_TAG.TAGS_ID "
            + "AND BOOKMARK.ID = BOOKMARK_TAG.BOOKMARKS_ID AND BOOKMARK.USER_ID = ? AND TAG.ID = ?";


    public static List<Bookmark> getBookmarks(User user) throws SQLException {
        List<Bookmark> list = new ArrayList<Bookmark>();
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_READ_BOOKMARKS);
            stmt.setLong(1, user.getId());
            ResultSet result = stmt.executeQuery();
            while (result.next()){
                long id = result.getLong(1);
                String description = result.getString(3);
                String title = result.getString(2);
                String link = result.getString(4);
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
        System.out.println("bookMarkId : " + bookmarkId);
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

    public static Bookmark getBookmarkById(Long id, User user) throws SQLException {
        List<Bookmark> list = getBookmarks(user);
        // Itere sur les bookmarks pour trouver celui avec id
        for (Bookmark bookmark : list) {
            if (bookmark.getId() == id) return bookmark;
        }
        return null;
    }


}
