package com.mfrancetic.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_snap.*

class SnapActivity : AppCompatActivity() {

    private var emails: ArrayList<String> = ArrayList()
    private var snaps: ArrayList<DataSnapshot> = ArrayList()
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
        all_snaps_list_view.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val displaySnapIntent = Intent(this, ViewSnapActivity::class.java)

                val snapshot = snaps[position]
                displaySnapIntent.putExtra(Constants.MESSAGE_KEY, snapshot.child(Constants.MESSAGE_KEY).value as String)
                displaySnapIntent.putExtra(Constants.IMAGE_NAME_KEY, snapshot.child(Constants.IMAGE_NAME_KEY).value as String)
                displaySnapIntent.putExtra(Constants.IMAGE_URL_KEY, snapshot.child(Constants.IMAGE_URL_KEY).value as String)
                displaySnapIntent.putExtra(Constants.SNAP_KEY, snapshot.key)
                startActivity(displaySnapIntent)
            }
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
                        snaps.add(p0)
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
                        for ((index, snap) in snaps.withIndex()) {
                            if (snap.key == p0.key) {
                                snaps.removeAt(index)
                                emails.remove(emails[index])
                                adapter?.notifyDataSetChanged()
                            }
                        }
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