package com.example.kotlinmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

//        LinearLayoutManager 설정은 kotlin 파일에서 하는것보다는 xml파일에서 해주는 것이 더 낫다고 한다... 왜지?
//        recyclerview_newmessage.layoutManager = LinearLayoutManager(this)

//        val adapter = GroupAdapter<GroupieViewHolder>()
//
////        adapter에 어떤 item 넣을것인지
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//
//        recyclerview_newmessage.adapter = adapter

        fetchUsers()
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        val ref =FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user!=null){
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
//                    여기 인텐트에 들어가는 context가 불확실해서? view를 통해서 가져와줌..아마도?
                    val intent = Intent(view.context,ChatLogActivity::class.java)
//                    intent.putExtra(USER_KEY, userItem.user.username)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)

//                    back버튼 누르면 메인페이지로 돌아가도록(채팅목록페이지)
                    finish()
                }

                recyclerview_newmessage.adapter=adapter
            }

        } )
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        will be called in our list for each user object later on..
        viewHolder.itemView.username_textview_new_message.text=user.username

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_new_message)
    }

}

//tis is super tedious
//
//class CustomAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>{
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getItemCount(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//}