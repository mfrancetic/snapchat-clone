package com.mfrancetic.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_snap.*
import kotlinx.android.synthetic.main.activity_snap_users.*

class SnapActivity : AppCompatActivity() {

    private var emails: ArrayList<String> = ArrayList()
    private var adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)

        setupListView()
        showAllSnaps()
    }

    private fun setupListView() {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        all_snaps_list_view.adapter = adapter
    }

    private fun showAllSnaps() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance().reference
        if (currentUserUid != null) {
            database.child(Constants.USERS_KEY)
                .child(currentUserUid).child(Constants.SNAPS_KEY)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        val email = p0.child(Constants.FROM_KEY).value as String
                        emails.add(email)
                        adapter?.notifyDataSetChanged()
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    }

                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                    }
                })
        }
    }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            val menuInflater: MenuInflater = menuInflater
            menuInflater.inflate(R.menu.main, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val itemId = item.itemId
            if (itemId == R.id.logout) {
                logoutUser()
                return true
            } else if (itemId == R.id.new_snap) {
                openNewSnapView()
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        private fun openNewSnapView() {
            val openNewSnapIntent = Intent(this, NewSnapActivity::class.java)
            startActivity(openNewSnapIntent)
        }

        private fun logoutUser() {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, getString(R.string.logout_successful), Toast.LENGTH_SHORT)
                .show()
            val goBackToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(goBackToMainActivity)
        }
    }