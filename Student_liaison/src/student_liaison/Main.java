package student_liaison;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;

public class Main {

	static Scanner sc = new Scanner(System.in);
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String URL = "jdbc:mysql://localhost:3306/student_liaison";
	static final String USER = "root";
	static final String PASSWORD = "password";

	static Connection con = null;
	static Statement s = null;
	static PreparedStatement ps = null;
	static ResultSet rs = null;
	static CallableStatement cs = null;

	public static void main(String[] args) {
		try {
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(URL, USER, PASSWORD);

			main_home_page();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void main_home_page() {
	    String instance_id = ""; // s_id or "admin"
	    boolean success = false;

	    do {
	        int userType = chooseUserType();
	        switch (userType) {
	            case 1: // User is student
	                instance_id = signin_page();
	                if (instance_id != null && !instance_id.isEmpty()) {
	                    student_page(instance_id);
	                    success = true; // Successful login
	                } else {
	                    System.out.println("Student login failed. Please try again.");
	                }
	                break;

	            case 2: // User is admin
	                sc.nextLine(); // Clear the buffer
	                String admin_password = "admin123"; // Hardcoded admin password
	                System.out.println("Signing as admin");
	                System.out.println("Enter Password: ");
	                String password = sc.nextLine();

	                if (password.equals(admin_password)) {
	                    System.out.println("Admin login successful");
	                    instance_id = "admin";
	                    success = true; // Successful admin login
	                } else {
	                    System.out.println("Wrong password. Try again.");
	                }
	                break;

	            default:
	                System.out.println("INVALID OPTION! Please choose a valid user type.");
	                break;
	        }
	    } while (!success);

	    // instance_id set, proceed to the respective page
	    if (instance_id.equals("admin")) {
	        admin_page();
	    } else {
	        student_page(instance_id);
	    }
	}


	static String signin_page() {
	    System.out.println("Signing as Student");
	    String instance_id = " ";
	    boolean success = false;

	    do {
	        try {
	            System.out.println("Choose 1. Signup 2. Login: ");
	            int signinChoice = sc.nextInt();

	            switch (signinChoice) {
	                case 1: // Signup
	                    String call_signup = "{? = CALL signup(?,?,?,?)}";
	                    cs = con.prepareCall(call_signup);
	                    cs.registerOutParameter(1, java.sql.Types.VARCHAR);

	                    // Prompt and take all the entries
	                    sc.nextLine(); // Consume leftover newline
	                    System.out.println("Enter ID: ");
	                    String id = sc.nextLine();
	                    System.out.println("Enter Name: ");
	                    String name = sc.nextLine();
	                    System.out.println("Enter Email: ");
	                    String email = sc.nextLine();
	                    System.out.println("Enter Password: ");
	                    String password = sc.nextLine();

	                    // Simple input validation
	                    if (id.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
	                        System.out.println("All fields are required.");
	                        continue;
	                    }

	                    cs.setString(2, id);
	                    cs.setString(3, name);
	                    cs.setString(4, email);
	                    cs.setString(5, password);

	                    cs.execute();

	                    String signupResult = cs.getString(1);
	                    if (signupResult != null) {
	                        System.out.println("Signup of " + signupResult + " successful");
	                        success = true;
	                        instance_id = signupResult;
	                    }
	                    break;

	                case 2: // Login
	                    String call_login = "{? = CALL login(?,?)}";
	                    cs = con.prepareCall(call_login);
	                    cs.registerOutParameter(1, java.sql.Types.VARCHAR);

	                    // Prompt and take all the entries
	                    sc.nextLine(); // Consume leftover newline
	                    System.out.println("Enter Email: ");
	                    String email_login = sc.nextLine();
	                    System.out.println("Enter Password: ");
	                    String password_login = sc.nextLine();

	                    // Simple input validation
	                    if (email_login.isEmpty() || password_login.isEmpty()) {
	                        System.out.println("Email and Password are required.");
	                        continue;
	                    }

	                    cs.setString(2, email_login);
	                    cs.setString(3, password_login);

	                    cs.execute();

	                    String result = cs.getString(1);
	                    if (result != null && !result.equals("NULL")) {
	                        System.out.println("Login of " + result + " successful");
	                        success = true;
	                        instance_id = result;
	                    } else {
	                        System.out.println("Wrong email or password.");
	                        success = false;
	                    }
	                    break;

	                default:
	                    System.out.println("INVALID OPTION! Please choose either 1 or 2.");
	                    success = false;
	                    break;
	            }
	        } catch (SQLException e) {
	            // Catch and display the SQL error
	            System.out.println("SQL Error: " + e.getMessage());
	            success = false;
	        } catch (Exception e) {
	            // Catch any other exceptions
	            e.printStackTrace();
	            success = false;
	        }
	    } while (!success);  // Repeat until successful login or signup

	    return instance_id;
	}
	static int chooseUserType() {
	    System.out.println("Choose 1. Student 2. Admin: ");
	    int userChoice = sc.nextInt();
	    return userChoice;
	}


	static void student_page(String instance_id) {
		try {
			System.out.println("Student homepage");
			int ch;
			do {
				System.out.println("MENU:  1. Upload certificate  2. View rejected certificates  0. Go back");
				ch = sc.nextInt();
				switch (ch) {
				case 1: // upload certificate
					// enter faculty coo name > enter location details > enter event details use its
					// e_id and > enter the certificate details
					sc.nextLine();
					System.out.println("Enter event location details:");
					System.out.print("Enter event location address (place,street,area): ");
					String l_address = sc.nextLine();
					System.out.print("Enter event location city: ");
					String l_city = sc.nextLine();
					System.out.print("Enter event location state: ");
					String l_state = sc.nextLine();
					System.out.print("Enter event location country: ");
					String l_country = sc.nextLine();

					String insert_location_q = "Insert into location(l_address,l_city,l_state,l_country) values(?,?,?,?)";
					ps = con.prepareStatement(insert_location_q);
					ps.setString(1, l_address);
					ps.setString(2, l_city);
					ps.setString(3, l_state);
					ps.setString(4, l_country);

					ps.execute();
					System.out.println("Event location details inserted");

					int l_id;
					String get_lid_q = "Select l_id from location where l_address=? AND l_city=?";
					ps = con.prepareStatement(get_lid_q);
					ps.setString(1, l_address);
					ps.setString(2, l_city);
					rs = ps.executeQuery();
					rs.next();
					l_id = rs.getInt(1);

					System.out.println("Enter details of the faculty coordinator or the mentor of the event");
					System.out.print("Enter faculty coordinator name: ");
					String fc_name = sc.nextLine();
					System.out.print("Enter faculty coordinator department: ");
					String fc_dept = sc.nextLine();

					String fc_id;
					String get_fcid_q = "Select fc_id from faculty_coordinator where fc_name=? AND fc_department=?";
					ps = con.prepareStatement(get_fcid_q);
					ps.setString(1, fc_name);
					ps.setString(2, fc_dept);
					rs = ps.executeQuery();
					rs.next();
					fc_id = rs.getString(1);

					System.out.println("Enter event details:");
					System.out.print("Enter event name: ");
					String e_name = sc.nextLine();
					System.out.print("Enter event organiser name: ");
					String e_org = sc.nextLine();
					System.out.print("Enter event date (YYYY-MM-DD format) : ");
					String e_datestr = sc.nextLine();
					java.sql.Date e_date = java.sql.Date.valueOf(e_datestr);
					System.out.print("Enter event category: ");
					String e_category = sc.nextLine();

					String insert_event_q = "Insert into event(e_name,e_organisation,e_date,e_category,l_id,fc_id) values(?,?,?,?,?,?)";
					ps = con.prepareStatement(insert_event_q);
					ps.setString(1, e_name);
					ps.setString(2, e_org);
					ps.setDate(3, e_date);
					ps.setString(4, e_category);
					ps.setInt(5, l_id);
					ps.setString(6, fc_id);

					ps.execute();
					System.out.println("Event details inserted");

					int e_id;
					String get_eid_q = "Select e_id from event where e_name=? AND e_organisation=? AND e_date=? AND fc_id=?";
					ps = con.prepareStatement(get_eid_q);
					ps.setString(1, e_name);
					ps.setString(2, e_org);
					ps.setDate(3, e_date);
					ps.setString(4, fc_id);
					rs = ps.executeQuery();
					rs.next();
					e_id = rs.getInt(1);

					System.out.println("Enter certificate details:");

					System.out.print("Enter certificate title name: ");
					String c_name = sc.nextLine();
					System.out.print("Enter date when certificate recieved: ");
					String c_datestr = sc.nextLine();
					java.sql.Date c_date = java.sql.Date.valueOf(c_datestr);
					System.out.print(
							"Upload the certificate on drive, allow access to the same and share its link below ");
					System.out.println("Enter drive link of the uploaded certificate: ");
					String c_link = sc.nextLine();
					System.out.print("Enter certificate type: ");
					String c_type = sc.nextLine();

					String insert_certificate_q = "Insert into certificate(c_name, c_date, c_link, c_type, e_id, s_id) values(?,?,?,?,?,?)";
//					ps = con.prepareStatement(insert_certificate_q);
//					ps.setString(1, c_name);
//					ps.setDate(2, c_date);
//					ps.setString(3, c_link);
//					ps.setString(4, c_type);
//					ps.setInt(5, e_id);
//					ps.setString(6, instance_id);
//
//					System.out.println("Certificate uploaded");

					try (PreparedStatement ps = con.prepareStatement(insert_certificate_q)) {
						ps.setString(1, c_name);
						ps.setDate(2, c_date);
						ps.setString(3, c_link);
						ps.setString(4, c_type);
						ps.setInt(5, e_id);
						ps.setString(6, instance_id);

						ps.execute();
						System.out.println("Certificate uploaded");
					} catch (SQLException e) {
						System.out.println("Error while inserting certificate: " + e.getMessage());
					}

					break;

				case 2: // view rejected certificates
					String display_rejected_q = "Select ec_name, ec_date, ec_type, ec_status, ec_link from rejectedCertificates where es_id=?";
					ps = con.prepareStatement(display_rejected_q);
					ps.setString(1, instance_id);
					rs = ps.executeQuery();
					int x = 1;
					ArrayList<String> links = new ArrayList<>();

					System.out.println("sr  -  name/title  -  date  -  type  -  status  -  link");

					while (rs.next()) {
						String name = rs.getString(1);
						java.sql.Date date = rs.getDate(2);
						String type = rs.getString(3);
						String status = rs.getString(4);
						String link = rs.getString(5);
						System.out
								.println(x + ".  " + name + "  " + date + "  " + type + "  " + status + "  \n " + link);
						x++;
						links.add(link);
					}
					System.out.println("Enter the sr of the link to be opened");
					int sr = sc.nextInt();

					view(links);
					break;

				case 0: // go to main page, choosing user page
					main_home_page();

				default:
					break;
				}

			} while (ch != 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void admin_page() {
		try {
			System.out.println("Admin homepage");
			int ch;
			do {
				System.out.println(
						"MENU:  1. Approve Certificates   2. View certificates (sorts n counts)   3.Search   0. Go back");
				ch = sc.nextInt();
				switch (ch) {
				case 1: // approval or rejection of certificates
					String call_countof_pending = "{? = CALL countofpending()}";
					cs = con.prepareCall(call_countof_pending);
					cs.registerOutParameter(1, Types.INTEGER);
					cs.execute();
					int cntP = cs.getInt(1);
					System.out.println("Count of Pending Certificates: " + cntP);

					String approval_pending_display_q = "Select s_id, c_id, c_name, e_name, e_category, c_link from viewPendingCertificates";
					ps = con.prepareStatement(approval_pending_display_q);
					rs = ps.executeQuery();
					System.out.println(
							"Sr   Student_id   Certificate_id   Certificate_Name   Event_name   Event_category");

					int xa = 1;
					ArrayList<String> linksa = new ArrayList<>();

					while (rs.next()) {
						String sid = rs.getString(1);
						int cid = rs.getInt(2);
						String cname = rs.getString(3);
						String ename = rs.getString(4);
						String ecategory = rs.getString(5);
						String link = rs.getString(6);

						System.out.println(xa + ".  " + sid + "  " + cid + "  " + cname + "  " + ename + "  "
								+ ecategory + "   " + link);

						xa++;
						linksa.add(link);
					}

					int linkselected_ch = 0;
					do {

						System.out.println("Choose  1-View Link  2-Change Status  0-Go back");
						linkselected_ch = sc.nextInt();

						switch (linkselected_ch) {

						case 1:
							System.out.println("Enter the sr of the link to be opened");
							int sr_o = sc.nextInt();

							view(linksa);
							break;

						case 2:
							System.out.println("Enter id of certificate");
							int sr_u = sc.nextInt(); // Certificate ID

							System.out.println("Type  1-APPROVED  2-REJECTED");
							int status_choice = sc.nextInt();
							String new_status = "";

							// Get status input
							do {
							    if (status_choice == 1) {
							        new_status = "APPROVED";
							        break;
							    } else if (status_choice == 2) {
							        new_status = "REJECTED";
							        break;
							    } else {
							        System.out.println("Invalid choice. Try again.");
							        status_choice = sc.nextInt(); // Prompt again
							    }
							} while (status_choice != 1 && status_choice != 2);

							// Prepare and execute the stored procedure
							try {
							    String call_updateCertificateStatus = "{CALL updateCertificateStatus(?, ?)}";
							    cs = con.prepareCall(call_updateCertificateStatus);
							    cs.setInt(1, sr_u);         // Set certificate ID
							    cs.setString(2, new_status); // Set new status

							    // Use executeUpdate for update operations
							    int rowsAffected = cs.executeUpdate();

							    if (rowsAffected > 0) {
							        System.out.println("Status updated successfully");
							    } else {
							        System.out.println("Record to be updated not found");
							    }
							} catch (SQLException e) {
							    System.out.println("Error: " + e.getMessage());
							}


						case 0:
							break;

						default:
							System.out.println("INVALID");

						}

					} while (linkselected_ch != 0);

					break;

				case 2: // view certificates
					int view_options_ch = 0;
					do {
						System.out.print(
								"MENU: 1-View all the approved certificates \n      2-View particular category \n      3-View particular city  \n      4-New to Old  \n      5-Old to New  \n      6-View particular certificate type");
						view_options_ch = sc.nextInt();

						switch (view_options_ch) {

						case 1: // View all the approved certificates, nonpending
							String call_countof_nonpending = "{? = CALL countofnonpending()}";
							cs = con.prepareCall(call_countof_nonpending);
							cs.registerOutParameter(1, Types.INTEGER);
							cs.execute();
							int cntNP = cs.getInt(1);
							System.out.println("Count of Approved Certificates: " + cntNP);

							String nonpending_display_q = "Select s_id, c_id, c_name, e_name, e_category, c_link from viewNonPendingCertificates";
							ps = con.prepareStatement(nonpending_display_q);
							rs = ps.executeQuery();
							System.out.println(
									"Sr   Student_id   Certificate_id   Certificate_Name   Event_name   Event_category");

							int xv = 1;
							ArrayList<String> linksv = new ArrayList<>();

							while (rs.next()) {
								String sid = rs.getString(1);
								int cid = rs.getInt(2);
								String cname = rs.getString(3);
								String ename = rs.getString(4);
								String ecategory = rs.getString(5);
								String link = rs.getString(6);

								System.out.println(xv + ".  " + sid + "  " + cid + "  " + cname + "  " + ename + "  "
										+ ecategory + "  \n " + link);

								xv++;
								linksv.add(link);
							}

							int chv;
							do {
								System.out.print("Type:  1-View  0-Go back");
								chv = sc.nextInt();
								if (chv == 1) {
									view(linksv);
								} else {
									break;
								}
							} while (chv != 0);

							break;

						case 2: // view particular category
							System.out.println("Type:  1-Technical  2-Sports  3-Cultural");
							int cat = sc.nextInt();
							String category="";
							if (cat == 1) {
								category = "technical";
							} else if (cat == 2) {
								category = "sports";
							} else if (cat == 3) {
								category = "cultural";
							}

							String category_view = "{CALL sort_by_category(?)}";
							ps = con.prepareStatement(category_view);
							ps.setString(1, category);
							rs = ps.executeQuery();

							System.out.println("Sr   Student_id   Certificate_id   Certificate_Name   Event_name  ");

							int xc = 1;
							ArrayList<String> linksc = new ArrayList<>();

							while (rs.next()) {
								String sid = rs.getString(1);
								int cid = rs.getInt(2);
								String link = rs.getString(3);
								String ename = rs.getString(5);
								String eorg = rs.getString(6);

								System.out.println(
										xc + ".  " + sid + "  " + cid + "  " + link + "  " + ename + "  " + eorg);

								xc++;
								linksc.add(link);
							}
							int chc;
							do {
								System.out.print("Type:  1-View  0-Go back");
								chc = sc.nextInt();
								if (chc == 1) {
									view(linksc);
								} else {
									break;
								}
							} while (chc != 0);

							break;

						case 3: // view particular city
							System.out.println("Enter the name of the city: ");
							String city = sc.next();
							String ca_q = "{CALL sort_by_city(?)}";
							ps = con.prepareStatement(ca_q);
							ps.setString(1, city);
							rs = ps.executeQuery();

							System.out.println(
									"Sr   Student_id   Certificate_id   Certificate_Name   Event_name   Event_address  ");

							int xcity = 1;
							ArrayList<String> linkscity = new ArrayList<>();

							while (rs.next()) {
								String sid = rs.getString(1);
								int cid = rs.getInt(2);
								String link = rs.getString(3);
								String ename = rs.getString(4);
								String eaddress = rs.getString(5);

								System.out.println(xcity + ".  " + sid + "  " + cid + "  " + link + "  " + ename + "  "
										+ eaddress);

								xcity++;
								linkscity.add(link);
							}
							int chcity;
							do {
								System.out.print("Type:  1-View  0-Go back");
								chcity = sc.nextInt();
								if (chcity == 1) {
									view(linkscity);
								} else {
									break;
								}
							} while (chcity != 0);

							break;

						case 4: // new to old
							System.out.println("Displaying new to old: ");
							String n2o_q = "{CALL new_to_old_date()}";
							ps = con.prepareStatement(n2o_q);
							rs = ps.executeQuery();

							System.out.println(
									"Sr   Student_id   Certificate_id   Event_name   Event_city   Event_date  ");

							int xn2o = 1;
							ArrayList<String> linksn2o = new ArrayList<>();

							while (rs.next()) {
								String sid = rs.getString(1);
								int cid = rs.getInt(2);
								String link = rs.getString(3);
								String ename = rs.getString(4);
								String ecity = rs.getString(5);
								java.util.Date edate = rs.getDate(6);

								System.out.println(
										xn2o + ".  " + sid + "  " + cid + "  " + ename + "  " + ecity + "  " + edate);

								xn2o++;
								linksn2o.add(link);
							}
							int chn2o;
							do {
								System.out.print("Type:  1-View  0-Go back");
								chn2o = sc.nextInt();
								if (chn2o == 1) {
									view(linksn2o);
								} else {
									break;
								}
							} while (chn2o != 0);

							break;

						case 5: // old to new
							System.out.println("Displaying new to old: ");
							String o2n_q = "{CALL new_to_old_date()}";
							ps = con.prepareStatement(o2n_q);
							rs = ps.executeQuery();

							System.out.println(
									"Sr   Student_id   Certificate_id   Event_name   Event_city   Event_date  ");

							int xo2n = 1;
							ArrayList<String> linkso2n = new ArrayList<>();

							while (rs.next()) {
								String sid = rs.getString(1);
								int cid = rs.getInt(2);
								String link = rs.getString(3);
								String ename = rs.getString(4);
								String ecity = rs.getString(5);
								java.util.Date edate = rs.getDate(6);

								System.out.println(
										xo2n + ".  " + sid + "  " + cid + "  " + ename + "  " + ecity + "  " + edate);

								xo2n++;
								linkso2n.add(link);
							}
							int cho2n;
							do {
								System.out.print("Type:  1-View  0-Go back");
								cho2n = sc.nextInt();
								if (cho2n == 1) {
									view(linkso2n);
								} else {
									break;
								}
							} while (cho2n != 0);

							break;

						case 6: // view particular certificate type
							System.out.println("Type:  1-Win  2-Participation ");
							int ct = sc.nextInt();
							String ctype = "";
							if (ct == 1) {
								ctype = "win";
							} else if (ct == 2) {
								ctype = "participation";
							}

							String ctype_view = "{CALL certificate_type(?)}";
							ps = con.prepareStatement(ctype_view);
							ps.setString(1, ctype);
							rs = ps.executeQuery();

							System.out.println("Sr   Student_id   Certificate_id   Event_name  ");

							int xct = 1;
							ArrayList<String> linksct = new ArrayList<>();

							while (rs.next()) {
								String sid = rs.getString(1);
								int cid = rs.getInt(2);
								String link = rs.getString(3);
								String ename = rs.getString(4);

								System.out.println(xct + ".  " + sid + "  " + cid + "  " + ename);

								xct++;
								linksct.add(link);
							}
							int chct;
							do {
								System.out.print("Type:  1-View  0-Go back");
								chct = sc.nextInt();
								if (chct == 1) {
									view(linksct);
								} else {
									break;
								}
							} while (chct != 0);

							break;

						default:
							System.out.println("invalid");

						}
					} while (view_options_ch != 0);
					break;

				case 3: // search certificates
					int option;
					do {
						System.out.println("Search Options:");
						System.out.println("1. Search Certificates by Student ID");
						System.out.println("2. Search Certificates between Dates");
						System.out.println("0. Go Back");
						System.out.print("Choose an option: ");
						option = sc.nextInt();
						sc.nextLine(); // Consume newline

						switch (option) {
						case 1: // Search certificates by student ID
							System.out.println("Enter UCE no of student to be searched: ");
							String s_id = sc.nextLine();
							String searchQuery = "SELECT c_id, c_name, c_date, c_link, c_type, c_status FROM certificate WHERE s_id = ?";
							try (PreparedStatement ps = con.prepareStatement(searchQuery)) {
								ps.setString(1, s_id);
								ResultSet rs = ps.executeQuery();
								ArrayList<String> linkSearch = new ArrayList<>();
								int xs = 1;

								// Display results
								System.out.println("Certificates for s_id: " + s_id);
								while (rs.next()) {
									int c_id = rs.getInt(1);
									String c_name = rs.getString(2);
									java.util.Date c_date = rs.getDate(3);
									String link = rs.getString(4);
									String c_type = rs.getString(5);
									String c_status = rs.getString(6);

									System.out.println(xs + ". " + c_id + "  " + c_name + "  " + c_date + "  " + c_type
											+ "  " + c_status);
									linkSearch.add(link);
									xs++;
								}
								// View links
								int chs;
								do {
									System.out.print("Type:  1-View  0-Go back: ");
									chs = sc.nextInt();
									sc.nextLine(); // Consume newline
									if (chs == 1) {
										view(linkSearch);
									} else {
										break;
									}
								} while (chs != 0);
							}
							break;

						case 2: // Search certificates between dates
							System.out.println("Enter the start date (YYYY-MM-DD):");
							String startDate = sc.nextLine();
							System.out.println("Enter the end date (YYYY-MM-DD):");
							String endDate = sc.nextLine();

							String callProcedure = "{CALL searchBwnDate(?, ?)}";
							try (CallableStatement cs = con.prepareCall(callProcedure)) {
								cs.setString(1, startDate);
								cs.setString(2, endDate);
								ResultSet rs = cs.executeQuery();
								ArrayList<String> linksBetweenDates = new ArrayList<>();
								int xsbd = 1;

								// Display results
								while (rs.next()) {
									String sid = rs.getString(1);
									int cid = rs.getInt(2);
									String cname = rs.getString(3);
									java.sql.Date cdate = rs.getDate(4);
									String link = rs.getString(5);
									String ename = rs.getString(6);

									System.out.println(xsbd + ". " + sid + "  " + cname + "  " + cdate + "  " + ename);
									linksBetweenDates.add(link);
									xsbd++;
								}
								// View links
								int chsbd;
								do {
									System.out.print("Type:  1-View  0-Go back: ");
									chsbd = sc.nextInt();
									sc.nextLine(); // Consume newline
									if (chsbd == 1) {
										view(linksBetweenDates);
									} else {
										break;
									}
								} while (chsbd != 0);
							} catch (SQLException e) {
								e.printStackTrace();
							}
							break;

						case 0: // Exit
							System.out.println("Going back...");
							break;

						default:
							System.out.println("Invalid option. Please choose again.");
						}
					} while (option != 0);
					break;

				case 0: // go to main page, choosing user page
					main_home_page();
					break;
				}
			} while (ch != 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void view(ArrayList<String> links) {
		System.out.println("Enter the sr of the link to be opened");
		int sr_o = sc.nextInt();

		if (sr_o > 0 && sr_o < links.size()) {
			String selectedLink = links.get(sr_o - 1);
			System.out.println("Opening link: " + selectedLink);
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.browse(new URI(selectedLink));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Opening links is not supported on your system.");
			}
		} else {
			System.out.println("No such sr found");
		}
	}

}
