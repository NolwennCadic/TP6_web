import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
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

	/**
	 * Provides the tags of a user.
	 *
	 * @param user
	 *           a user
	 * @return user tags
	 * @throws SQLException
	 *            if the DB connection fails
	 */
	public static List<Tag> getTags(User user) throws SQLException {
		List<Tag> list = new ArrayList<Tag>();
		Connection conn = DBConnection.getConnection();
		try{
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
		} finally{conn.close();}
	}
	//TODO
	public Tag  getTagByName(String name, User user) throws SQLException {
		List<Tag> list = getTags(user);
		// Itere sur les tags pour trouver celuin avec le nom name
		for (Tag tag : list) {
		    if (tag.getName() == name) return tag;
        }
        return null;
	}

    public void saveTag(Tag tag, User user) throws SQLException {
	    Connection conn = DBConnection.getConnection();
	    // Ouvre la connection et insert le nouveau tag
        try {
            String SQL_INSERT_TAG = "INSERT INTO Tag(`name`, `user_id`) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_TAG);
            stmt.setString(1, tag.getName());
            stmt.setLong(2, user.getId());
        } finally{conn.close();}
    }
}
