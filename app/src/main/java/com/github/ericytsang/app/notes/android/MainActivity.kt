package com.github.ericytsang.app.notes.android

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Closeable
import java.time.MonthDay
import java.time.Year

class MainActivity : AppCompatActivity()
{

    private var created:Created? = null
        set(value)
        {
            field?.close()
            field = value
        }

    override fun onCreate(savedInstanceState:Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        created = Created(this)
    }

    override fun onDestroy()
    {
        created = null
        super.onDestroy()
    }

    private class Created(context:MainActivity):Closeable
    {
        override fun close() = Unit

        val layout = Layout(context.findViewById(android.R.id.content))
        val viewModel:UserViewModel = ViewModelProvider(
                context,
                ViewModelProvider.AndroidViewModelFactory.getInstance(context.application)
        ).get(UserViewModel::class.java)

        init
        {
            val userViewModesl = listOf(
                    ViewModelProvider(
                            context,
                            ViewModelProvider.AndroidViewModelFactory.getInstance(context.application)
                    ).get(UserViewModel::class.java),
                    ViewModelProvider(
                            context,
                            ViewModelProvider.AndroidViewModelFactory.getInstance(context.application)
                    ).get(UserViewModel::class.java),
                    ViewModelProvider(
                            context,
                            ViewModelProvider.AndroidViewModelFactory.getInstance(context.application)
                    ).get(UserViewModel::class.java)
            )

            if (!userViewModesl.all {it == userViewModesl.first()})
            {
                throw IllegalAccessException()
            }

            viewModel.selectedUser.observe(context,Observer()
            {user:User? ->
                layout.text.text = user?.values?.name?.run {"$firstName $lastName"} ?: "no user selected"
            })
        }


        fun randomizeId()
        {
            viewModel.randomizeId()
        }
    }

    fun randomizeId(view:View)
    {
        created?.randomizeId()
    }
}

class Layout(
        override val containerView:View)
    :LayoutContainer

class UserViewModel(
        application:Application)
    :AndroidViewModel(application)
{
    private val userRepo = UserRepo()

    val selectedUser = MutableLiveData<User?>()

    fun randomizeId()
    {
        selectedUser.postValue(userRepo.getUser(userRepo.getRandomUserId()))
    }
}

data class User(
        val id:Id,
        val values:Values)
{
    data class Id(
            val id:String)

    data class Values(
            val name:Name,
            val alias:String,
            val birthday:YyyyMmDd,
            val email:Email)
}

data class Name(
        val firstName:String,
        val lastName:String)

data class Email(
        /** e.g. surplus.et */
        val username:String,
        /** e.g. google */
        val domainName:String,
        /** e.g. com */
        val topLevelDomain:String)

data class YyyyMmDd(
        val year:Year,
        val monthDay:MonthDay)

class UserRepo
{
    private val allUsers = listOf(
            User(User.Id("1"),User.Values(Name("Eric","Tsang"),"etsang",YyyyMmDd(Year.of(1994),MonthDay.of(8,8)),Email("surplus.et","gmail","com"))),
            User(User.Id("2"),User.Values(Name("Alex","Cheng"),"kiko",YyyyMmDd(Year.of(1994),MonthDay.of(7,27)),Email("kiko0727","hotmail","com"))),
            User(User.Id("3"),User.Values(Name("June","Wong"),"tofu",YyyyMmDd(Year.of(1994),MonthDay.of(6,8)),Email("juneealj","hotmail","com"))),
            User(User.Id("4"),User.Values(Name("Trevor","Fujiwara"),"fuji",YyyyMmDd(Year.of(1994),MonthDay.of(6,13)),Email("trevor.fuji","gmail","com"))),
            User(User.Id("5"),User.Values(Name("Annie","Dupont"),"annie",YyyyMmDd(Year.of(1998),MonthDay.of(6,18)),Email("anniexk.d","gmail","com"))),
            User(User.Id("6"),User.Values(Name("Holly","Sun"),"hollyzero",YyyyMmDd(Year.of(1991),MonthDay.of(12,12)),Email("holly.sun.1991","gmail","com"))),
            User(User.Id("7"),User.Values(Name("Betty","Tse"),"msbettytse",YyyyMmDd(Year.of(1962),MonthDay.of(5,14)),Email("msbettytse","gmail","com"))),
            User(User.Id("8"),User.Values(Name("Karl","Tsang"),"ktsang",YyyyMmDd(Year.of(1958),MonthDay.of(8,1)),Email("7299119","gmail","com"))),
            User(User.Id("9"),User.Values(Name("Heather","Tsang"),"feather",YyyyMmDd(Year.of(1988),MonthDay.of(10,22)),Email("heeja.t","gmail","com"))))

    fun getUser(userId:User.Id):User?
    {
        return allUsers.find { it.id == userId }
    }

    fun getLiveDataUser(userId:User.Id):LiveData<User>
    {
        val userToFetch = MutableLiveData<User>()
        userToFetch.postValue(allUsers.find { it.id == userId })
        return userToFetch
    }

    fun getRandomUserId():User.Id
    {
        return allUsers.map {it.id}.random()
    }

    fun firstUserId():LiveData<User.Id>
    {
        val userIdToFetch = MutableLiveData<User.Id>()
        userIdToFetch.postValue(allUsers.first().id)
        return userIdToFetch
    }
}
