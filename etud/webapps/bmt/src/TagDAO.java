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

	//Récupère le tag qui a un nom donné
	public static Tag  getTagByName(String name, User user) throws SQLException {
		List<Tag> list = getTags(user);
		// Itere sur les tags pour trouver celui avec le nom name
		for (Tag tag : list) {
		    if (tag.getName() == name) return tag;
        }
        return null;
	}

	//Récupère le tag qui a un ID donné
	public static Tag  getTagById(Long id, User user) throws SQLException {
		List<Tag> list = getTags(user);
		// Itere sur les tags pour trouver celui avec le tag id
		for (Tag tag : list) {
			if (tag.getId() == id) return tag;
		}
		return null;
	}

	//Enregistre un tag sur la base de donnée
    public static void saveTag(Tag tag, User user) throws SQLException {
	    Connection conn = DBConnection.getConnection();
	    // Ouvre la connection et insert le nouveau tag
        try {
            String SQL_INSERT_TAG = "INSERT INTO Tag(`name`, `user_id`) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_TAG);
            stmt.setString(1, tag.getName());
            stmt.setLong(2, user.getId());
            stmt.executeUpdate();
        } finally{conn.close();}
    }
}
