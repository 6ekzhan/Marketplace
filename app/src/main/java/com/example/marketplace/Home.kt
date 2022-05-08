package com.example.marketplace

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.Model.Products
import com.example.marketplace.Prevalent.Prevalent
import com.example.marketplace.ViewHolder.ProductViewHolder
import com.example.marketplace.databinding.ActivityHomeBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.paperdb.Paper


class Home : AppCompatActivity() {
    private var ProductsRef: DatabaseReference? = null
    private var recyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProductsRef = FirebaseDatabase.getInstance().reference.child("Products");

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Paper.init(this)

        setSupportActionBar(binding.appBarHome.toolbar)

        binding.appBarHome.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_cart, R.id.nav_orders, R.id.nav_categories, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            Paper.book().destroy()
            val intent = Intent(this@Home, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
            true
        }

        val navHeader: View? = navView.getHeaderView(0)
        val userNameTextView: TextView? = navHeader?.findViewById(R.id.user_name)
        val profileImageView: CircleImageView? = navHeader?.findViewById(R.id.profile_image)

        userNameTextView?.text = Prevalent.currentOnlineUser.name

        recyclerView = findViewById(R.id.recycler_menu2)
        recyclerView?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = layoutManager
    }

    override fun onStart() {
        super.onStart()
        val options = FirebaseRecyclerOptions.Builder<Products>()
            .setQuery(ProductsRef!!, Products::class.java)
            .build()
        val adapter: FirebaseRecyclerAdapter<Products, ProductViewHolder> =
            object : FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                override fun onBindViewHolder(
                    @NonNull holder: ProductViewHolder,
                    position: Int,
                    @NonNull model: Products
                ) {
                    holder.txtProductName.text = model.pname
                    holder.txtProductDescription.text = model.description
                    holder.txtProductPrice.text = "Price = " + model.price + "$"
                    Picasso.get().load(model.image).into(holder.imageView)
                }

                @NonNull
                override fun onCreateViewHolder(
                    @NonNull parent: ViewGroup,
                    viewType: Int
                ): ProductViewHolder {
                    val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.product_items_layout, parent, false)
                    return ProductViewHolder(view)
                }
            }
        recyclerView?.adapter = adapter
        adapter.startListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}