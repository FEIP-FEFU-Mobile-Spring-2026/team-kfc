package com.kfc.onlinestore.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kfc.onlinestore.model.CartItem

class CartRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cart", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getCart(): List<CartItem> {
        val json = prefs.getString("cart_items", "[]") ?: "[]"
        return try {
            gson.fromJson(json, Array<CartItem>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addToCart(productId: String, sizeId: String) {
        val cart = getCart().toMutableList()
        val existingItem = cart.find { it.productId == productId && it.sizeId == sizeId }

        if (existingItem != null) {
            cart[cart.indexOf(existingItem)] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            cart.add(CartItem(productId, sizeId, 1))
        }

        saveCart(cart)
    }

    fun updateQuantity(productId: String, sizeId: String, quantity: Int) {
        val cart = getCart().toMutableList()
        val index = cart.indexOfFirst { it.productId == productId && it.sizeId == sizeId }

        if (index != -1) {
            if (quantity > 0) {
                cart[index] = cart[index].copy(quantity = quantity)
            } else {
                cart.removeAt(index)
            }
            saveCart(cart)
        }
    }

    fun removeFromCart(productId: String, sizeId: String) {
        val cart = getCart().toMutableList()
        cart.removeAll { it.productId == productId && it.sizeId == sizeId }
        saveCart(cart)
    }

    fun clearCart() {
        saveCart(emptyList())
    }

    private fun saveCart(cart: List<CartItem>) {
        val json = gson.toJson(cart)
        prefs.edit().putString("cart_items", json).apply()
    }
}
