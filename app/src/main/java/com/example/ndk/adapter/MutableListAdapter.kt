package com.example.ndk.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


internal abstract class MutableListAdapter<T, VB : RecyclerView.ViewHolder>(
  private val items: MutableList<T> = ArrayList(),
) : RecyclerView.Adapter<VB>(), MutableList<T> by items {

  fun setItems(newItems: MutableList<T>): Unit = delegateAndDiff {
    clear()
    addAll(newItems)
  }

  private fun <E> delegateAndDiff(block: MutableList<T>.() -> E): E {
    val old = ArrayList(items)
    val result = items.block()
    calculateDiff(old, items, ::compareItems).dispatchUpdatesTo(this)
    return result
  }

  private fun <T> calculateDiff(
    oldItems: List<T>,
    newItems: List<T>,
    sameItems: (checkContent: Boolean, a: T, b: T) -> Boolean
  ) = DiffUtil.calculateDiff(createSimpleDiff(oldItems, newItems, sameItems))

  private fun <T> createSimpleDiff(
    oldItems: List<T>,
    newItems: List<T>,
    sameItems: (checkContent: Boolean, a: T, b: T) -> Boolean
  ) = object : DiffUtil.Callback() {
    override fun getOldListSize() = oldItems.size
    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
      sameItems(false, oldItems[oldItemPosition], newItems[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
      sameItems(true, oldItems[oldItemPosition], newItems[newItemPosition])
  }

  override fun add(element: T): Boolean = delegateAndDiff { add(element) }
  override fun remove(element: T): Boolean = delegateAndDiff { remove(element) }
  override fun addAll(elements: Collection<T>): Boolean = delegateAndDiff { addAll(elements) }
  override fun addAll(index: Int, elements: Collection<T>): Boolean =
    delegateAndDiff { addAll(index, elements) }

  override fun removeAll(elements: Collection<T>): Boolean = delegateAndDiff { removeAll(elements) }
  override fun retainAll(elements: Collection<T>): Boolean = delegateAndDiff { retainAll(elements) }
  override fun clear() = delegateAndDiff { clear() }
  override fun set(index: Int, element: T): T = delegateAndDiff { set(index, element) }
  override fun add(index: Int, element: T) = delegateAndDiff { add(index, element) }
  override fun removeAt(index: Int): T = delegateAndDiff { removeAt(index) }

  abstract fun compareItems(checkContent: Boolean, a: T, b: T): Boolean

  protected fun getItemAtPosition(position: Int) = items[position]

  override fun getItemCount() = items.size

}