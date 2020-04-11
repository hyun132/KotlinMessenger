package com.example.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    val TAG = "ChatLog"
    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser:User?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter=adapter

//        val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

//        setupDummyData()
        listenForMessage()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG,"Attempt to send message...")
            performSendMessage()
        }
    }

    private fun listenForMessage(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
//        /messages에 속해있는 모든 데이터들에 이벤트가 발생할 시 실행되는것같음
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage !=null){
                    Log.d(TAG,chatMessage?.text)
//                내가 보
                    if (chatMessage.fromId==FirebaseAuth.getInstance().uid){
                        val currentUser = LatesMessageActivity.currentUser ?:return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    }else{
                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun performSendMessage(){
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId=user.uid

        if(fromId == null) return

//        how to we actually send a message to firebase...
        var reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage=ChatMessage(reference.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat message:${reference.key}")
            }
            .addOnFailureListener {

            }
    }
}

class ChatToItem(val text: String, val user:User):Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text=text

        val uri = user.profileImageUrl
        val targetImageView=viewHolder.itemView.imageView_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

}

class ChatFromItem(val text:String,val user:User):Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

        val uri = user.profileImageUrl
        val targetImageView=viewHolder.itemView.imageView_from_row
        Picasso.get().load(uri).into(targetImageView)
    }

}
