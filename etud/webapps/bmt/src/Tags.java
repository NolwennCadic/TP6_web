import java.awt.print.Book;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Provides handling of tag-related requests.
 *
 * @author Jan Mikac
 */
public class Tags {
	/**
	 * Handles the request for the tag list.
	 *
	 * @param req
	 *           the request
	 * @param resp
	 *           the response
	 * @param method
	 *           request method to appply
	 * @param requestPath
	 *           request path
	 * @param queryParams
	 *           query parameters
	 * @param user
	 *           the user
	 * @throws IOException
	 *            if the response cannot be written
	 */
	public static void handleTagList(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException {
		// Rule-out PUT and DELETE requests
		System.out.println("Action: handleTagList - " + method + "-" + queryParams);
		if (method == Dispatcher.RequestMethod.PUT || method == Dispatcher.RequestMethod.DELETE) {
			resp.setStatus(405);
			return;
		}

		// Handle GET
		if (method == Dispatcher.RequestMethod.GET) {
			// Get the tag list
			List<Tag> tags = null;
			try {
				tags = TagDAO.getTags(user);
			} catch (SQLException ex) {
				resp.setStatus(500);
				return;
			}

			// Encode the tag list to JSON
			String json = "[";
			for (int i = 0, n = tags.size(); i < n; i++) {
				Tag tag = tags.get(i);
				json += tag.toJson();
				if (i < n - 1)
					json += ", ";
			}
			json += "]";

			// Send the response
			resp.setStatus(200);
			resp.setContentType("application/json");
			resp.getWriter().print(json);
			return;
		}

		// Handle POST
		if (method == Dispatcher.RequestMethod.POST) {
			// TODO 1
			// Recupere le string passe en parametre et le transforme en json
            System.out.println("Dans le 1er if");
            JSONObject jsonTag = new JSONObject(queryParams.get("json").get(0));
			// Recuperation du nom passé en paramètre
			String tagName = jsonTag.getString("name");
            try {
                System.out.println("TagDAO.getTagByName(tagName, user)" + TagDAO.getTagByName(tagName, user));
                if (TagDAO.getTagByName(tagName, user) == null) {
                    // Crée un tag
                    Tag tag = new Tag(tagName);
                    // Ajoute à la BD
                    TagDAO.saveTag(tag, user);
                    System.out.println("la");
                    resp.setStatus(200);

                } else {
                    // si le tag existe déjà, renvoie status d'erreur
                    resp.setStatus(403);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;

		}

		// Other
		resp.setStatus(405);
	}

	/**
	 * Pour cette fonction, on gère lorsque qu'un id est passé en chemin
	 *  Les methodes autorisées sont get, put et delete
	 *
	 * @param req
	 * @param resp
	 * @param method
	 * @param requestPath
	 * @param queryParams
	 * @param user
	 */
	public static void handleTag(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException{
		System.out.println("Action: handleTag - " + method + "-" + queryParams);
		// Rule-out POST request
		if (method == Dispatcher.RequestMethod.POST){
			resp.setStatus(405);
			return;
		}

		//handle GET
		if (method == Dispatcher.RequestMethod.GET){
			// Récupérer la liste des tags
//			List<Tag> tags = null;
//			try {
//				tags = TagDAO.getTags(user);
//			} catch (SQLException ex) {
//				resp.setStatus(500);
//				return;
//			}
			/* On récupère l'id que l'utilisateur a entré */
			Long id = (long) Integer.parseInt(requestPath[2]);
			try {
				if(TagDAO.getTagById(id, user) != null) {
				// Encode the tag list to JSON
				String json = "[";
				json += TagDAO.getTagById(id, user).toJson();
				json += "]";
				// Send the response
				resp.setStatus(200);
				resp.setContentType("application/json");
				resp.getWriter().print(json);
				return;
				}else {
                    resp.setStatus(404);
                    return;
                }
			}catch (SQLException ex) {
				resp.setStatus(500);
				return;
			}
		}
		
		//handle PUT
		if (method == Dispatcher.RequestMethod.PUT){
			System.out.println("modifier tag ");
			// Récupérer la liste des tags
			List<Tag> tags = null;
			try {
				tags = TagDAO.getTags(user);
			} catch (SQLException ex) {
				resp.setStatus(500);
				return;
			}
			/* On récupère l'id que l'utilisateur a entré */
			Long id = (long) Integer.parseInt(requestPath[2]);
			Tag tag ;
			try {
				if(TagDAO.getTagById(id, user) != null) {
				System.out.println("il ya bien un élement");
				JSONObject jsonTag = new JSONObject(queryParams.get("json").get(0));
				// Recuperation du nom passé en paramètre
				String newTagName = jsonTag.getString("name");
				//ON modifie le name et on updata la BD
				tag = TagDAO.getTagById(id, user);
				System.out.println("id " + id);
				System.out.println("newTagName "+ newTagName);
				TagDAO.updateTag(tag, newTagName,user);
				// Send the response
				resp.setStatus(204);
				resp.setContentType("application/json");
				return;
				}else {
                    resp.setStatus(403);
                    return;
                }
			}catch (SQLException ex) {
				resp.setStatus(500);
				return;
			}
		}
		
		//handle DELETE
		if (method == Dispatcher.RequestMethod.DELETE){
			System.out.println("delete tag ");
			// Récupérer la liste des tags
			List<Tag> tags = null;
			try {
				tags = TagDAO.getTags(user);
			} catch (SQLException ex) {
				resp.setStatus(500);
				return;
			}
			/* On récupère l'id que l'utilisateur a entré */
			Long id = (long) Integer.parseInt(requestPath[2]);
			try {
				if(TagDAO.getTagById(id, user) != null) {
				//ON modifie le name et on updata la BD
				System.out.println("hello there");
				TagDAO.deleteTag(id, user);
				// Send the response
				resp.setStatus(204);
				resp.setContentType("application/json");
				return;
				}else {
                    resp.setStatus(403);
                    return;
                }
			}catch (SQLException ex) {
				resp.setStatus(500);
				return;
			}
		}
		
	}

	/**
	 * TODO donne la liste des marque-pages attachés au tag
	 * dont l'ID est <tid>CR 200
	 *
	 * @param req
	 * @param resp
	 * @param method
	 * @param requestPath
	 * @param queryParams
	 * @param user
	 */
	public static void handleTagBookmarks(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException {

		System.out.println("Action: handleTagBookmarks - " + method + "-" + queryParams);
		// handle GET
		if (method == Dispatcher.RequestMethod.GET) {
			//Récupère id du tag
			Long id = (long) Integer.parseInt(requestPath[2]);
			List<Bookmark> bookmarks = null;
			List<Tag> tags = null;
			try {
				// si le tag existe bien
				if(TagDAO.getTagById(id, user) != null) {
					bookmarks = BookmarkDAO.getListOfBookmarkFromTag(id, user);
					//Encode the bookmarkList in json
					String json = "[";
					for (int i = 0, n = bookmarks.size(); i < n; i++) {
						Bookmark bookmark = bookmarks.get(i);
						json += bookmark.toJson();
						if (i < n - 1)
							json += ", ";
					}
					json += "]";
					// Send the response
					resp.setStatus(200);
					resp.setContentType("application/json");
					resp.getWriter().print(json);
					return;

				}
				else {
					System.out.println("Dans else");
					resp.setStatus(405);
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			resp.setStatus(405);
			return;
		}
	}

	/**
	 * TODO
	 * Gère l'attachement des tags aux bookmarks
	 *
	 * @param req
	 * @param resp
	 * @param method
	 * @param requestPath
	 * @param queryParams
	 * @param user
	 */
	public static void handleTagBookmark(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException {
		System.out.println("Action: handleTagBookmark - " + method + "-" + queryParams);
		// Handle the get
		if (method == Dispatcher.RequestMethod.GET) {
			// get the id of the bookmark
			Long bookmarkID = (long) Integer.parseInt(requestPath[4]);
			//get the tag of the bookmark
			Long tagId = (long) Integer.parseInt(requestPath[2]);
			try {
				Bookmark bookmark = BookmarkDAO.getBookmarkById(bookmarkID, user);
				Tag tag = TagDAO.getTagById(tagId, user);
				// Test if the given tag an bookmark exist
				if (bookmark != null && tag != null) {
					if (bookmark.containsTag(tagId)) {
						resp.setStatus(200);
						return;
					} else {
						resp.setStatus(404);
						return;
					}

				} else {
					resp.setStatus(404);
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (method == Dispatcher.RequestMethod.PUT) {
			// get the id of the bookmark
			Long bookmarkID = (long) Integer.parseInt(requestPath[4]);
			//get the tag of the bookmark
			Long tagId = (long) Integer.parseInt(requestPath[2]);
			Bookmark bookmark = null;
			try {
				bookmark = BookmarkDAO.getBookmarkById(bookmarkID, user);
				Tag tag = TagDAO.getTagById(tagId, user);
				// Test if the given tag an bookmark exist
				if (bookmark != null && tag != null) {
					if (bookmark.containsTag(tagId)) {
						// Ne fait rien
						resp.setStatus(304);
						return;
					} else {
						TagDAO.attachedBookMark(bookmarkID, tagId);
						resp.setStatus(204);
						return;
					}

				} else {
					resp.setStatus(403);
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (method == Dispatcher.RequestMethod.DELETE) {
			// get the id of the bookmark
			Long bookmarkID = (long) Integer.parseInt(requestPath[4]);
			//get the tag of the bookmark
			Long tagId = (long) Integer.parseInt(requestPath[2]);
			Bookmark bookmark = null;
			try {
				bookmark = BookmarkDAO.getBookmarkById(bookmarkID, user);
				Tag tag = TagDAO.getTagById(tagId, user);
				if (bookmark != null && tag != null) {
					if (bookmark.containsTag(tagId)) {
						TagDAO.removeAttachmentBookmark(bookmarkID, tagId);
						resp.setStatus(204);
						return;
					} else {
						resp.setStatus(403);
						return;
					}
				} else {
					resp.setStatus(403);
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			resp.setStatus(405);
			return;
		}
	}
}
