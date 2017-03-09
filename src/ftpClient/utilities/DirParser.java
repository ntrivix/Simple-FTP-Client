package ftpClient.utilities;

import java.util.ArrayList;

public class DirParser {
	public static ArrayList<String> parse(String r) {
		ArrayList<String> res = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			//StringBuilder p1 = new StringBuilder();
			int pos = r.indexOf(" ");
			res.add(r.substring(0,pos));
			while (r.charAt(pos) == ' '){
				pos++;
			}
			r = r.substring(pos);
			
		}
		res.add(r);
		return res;
	}
}
