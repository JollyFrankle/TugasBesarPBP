package com.example.tugasbesarpbp.entity

class User(private val id: Int, private val email: String, private val username: String, private val password: String, private val tglLahir: String, private val noTelp: String) {

    companion object{
        // static objects
        private var list: MutableList<User> = arrayListOf(
            User(1, "admin@www.com", "admin", "admin", "2022-01-01", "081234567890"),
            User(2, "user@www.com", "user", "user", "2022-01-01", "081234567890")
        )

        // get username and password
        fun getLogin(username: String, password: String): User? {
            for (user in list) {
                if (user.username == username && user.password == password) {
                    return user
                }
            }
            return null
        }

        // public function add to list of user
        fun add(user: User) {
            list.add(user)
        }

        // get list
        fun getList(): MutableList<User> {
            return list
        }
    }

    // function get username
    fun getUsername(): String {
        return username
    }

    // func get password
    fun getPassword(): String {
        return password
    }
}