//
//  SelectMultipleViewController.swift
//  Gateway
//
//  Created by Abhi Gupta on 2015-10-09.
//  Copyright © 2015 ScrypticLabs. All rights reserved.
//

import UIKit
import Parse
import Alamofire

protocol SelectMultipleViewControllerDelegate {
    func didSelectMultipleUsers(selectedUsers: [PFUser]!)
}

class SelectMultipleViewController: UITableViewController {
    
    var users = [PFUser]()
    var selection = [String]()
    var delegate: SelectMultipleViewControllerDelegate!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.loadUsers()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Backend methods
    
    func loadUsers() {
        let user = PFUser.currentUser()
        let query = PFQuery(className: PF_USER_CLASS_NAME)
        query.whereKey(PF_USER_OBJECTID, notEqualTo: user.objectId)
        query.orderByAscending(PF_USER_FULLNAME)
        query.limit = 1000
        query.findObjectsInBackgroundWithBlock { (objects: [AnyObject]!, error: NSError!) -> Void in
            if error == nil {
                self.users.removeAll(keepCapacity: false)
                self.users += objects as! [PFUser]!
                self.tableView.reloadData()
            } else {
                ProgressHUD.showError("Network error")
            }
        }
    }
    
    // MARK: - User actions
    
    @IBAction func cancelButtonPressed(sender: UIBarButtonItem) {
        self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    @IBAction func doneButtonPressed(sender: UIBarButtonItem) {
        if self.selection.count == 0 {
            ProgressHUD.showError("No recipient selected")
        } else {
            self.dismissViewControllerAnimated(true, completion: { () -> Void in
                var selectedUsers = [PFUser]()
                for user in self.users {
                    if self.selection.contains(user.objectId) {
                        selectedUsers.append(user)
                    }
                }
                selectedUsers.append(PFUser.currentUser())
                self.delegate.didSelectMultipleUsers(selectedUsers)
            })
        }
    }
    
    // MARK: - Table view data source
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // #warning Potentially incomplete method implementation.
        // Return the number of sections.
        return 1
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete method implementation.
        // Return the number of rows in the section.
        return self.users.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("cell", forIndexPath: indexPath)
        
        let user = self.users[indexPath.row]
        cell.textLabel?.text = user[PF_USER_FULLNAME] as? String
        
        let selected = self.selection.contains(user.objectId)
        cell.accessoryType = selected ? UITableViewCellAccessoryType.Checkmark : UITableViewCellAccessoryType.None
        
        return cell
    }
    
    // MARK: - Table view delegate
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        tableView.deselectRowAtIndexPath(indexPath, animated: true)
        
        let user = self.users[indexPath.row]
        let selected = self.selection.contains(user.objectId)
        if selected {
            if let index = self.selection.indexOf(user.objectId) {
                self.selection.removeAtIndex(index)
            }
        } else {
            self.selection.append(user.objectId)
        }
        
        self.tableView.reloadData()
    }
    
    
}
