package com.mfrancetic.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_snap_users.*

class SnapUsersActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var emails: ArrayList<String> = ArrayList()
    private var userKeys: ArrayList<String> = ArrayList()
    private var adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap_users)

        title = getString(R.string.choose_recipient)
        database = FirebaseDatabase.getInstance().reference

        setupLoadingView()
        getUsersFromDatabase()
        setupListView()
    }

    private fun setupListView() {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        users_list_view.adapter = adapter
        users_list_view.onItemClickListener = OnItemClickListener { parent, view, position, id
            ->
            createNewSnap(position)
        }
    }

    private fun createNewSnap(position: Int) {
        if (intent != null) {
            val fromEmail = FirebaseAuth.getInstance().currentUser!!.email!!
            val message = intent.getStringExtra(Constants.MESSAGE_KEY)
            val imageName = intent.getStringExtra(Constants.IMAGE_NAME_KEY)
            val imageUrl = intent.getStringExtra(Constants.IMAGE_URL_KEY)

            val snapMap: Map<String, String> = mapOf(
                Constants.FROM_KEY to fromEmail,
                Constants.IMAGE_NAME_KEY to imageName,
                Constants.IMAGE_URL_KEY to imageUrl,
                Constants.MESSAGE_KEY to message
            )
            database.child(Constants.USERS_KEY).child(userKeys[position]).child(Constants.SNAPS_KEY)
                // random ID will be generated
                .push().setValue(snapMap)

            Toast.makeText(this, getString(R.string.snap_sent), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, SnapActivity::class.java)
            // wipe everything from the history
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun getUsersFromDatabase() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        setupEmptyView()
        database.child(Constants.USERS_KEY)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val email = p0.child(Constants.EMAIL_ID).value as String
                    if (email != currentUserEmail) {
                        emails.add(email)
                        userKeys.add(p0.key.toString())
                        adapter?.notifyDataSetChanged()
                        removeEmptyView()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    val email = p0.child(Constants.EMAIL_ID).value as String
                    if (email != currentUserEmail) {
                        emails.remove(email)
                        userKeys.remove(p0.key.toString())
                        adapter?.notifyDataSetChanged()
                    }
                    if (emails.size < 1) {
                        setupEmptyView()
                    }
                }
            })
    }

    private fun removeEmptyView() {
        users_loading_indicator.visibility = View.GONE
        users_list_view.visibility = View.VISIBLE
        users_empty_image_view.visibility = View.GONE
        users_empty_text_view.visibility = View.GONE
    }

    private fun setupEmptyView() {
        users_loading_indicator.visibility = View.GONE
        users_empty_text_view.visibility = View.VISIBLE
        users_empty_image_view.visibility = View.VISIBLE
        users_list_view.visibility = View.INVISIBLE
    }

    private fun setupLoadingView() {
        users_loading_indicator.visibility = View.VISIBLE
        users_empty_image_view.visibility = View.GONE
        users_empty_text_view.visibility = View.GONE
        users_list_view.visibility = View.INVISIBLE
    }
}