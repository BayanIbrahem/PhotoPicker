package com.example.photopicker.domain.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.photopicker.databinding.ItemPhotoBinding
import com.example.photopicker.domain.utils.SharedStoragePhoto
import com.example.photopicker.domain.adapters.SharedPhotoAdapter.SharedStoragePhotoViewHolder

class SharedPhotoAdapter (val onPhotoLongClickListener: (photo: SharedStoragePhoto) -> Unit) : RecyclerView.Adapter<SharedStoragePhotoViewHolder>() {

    inner class SharedStoragePhotoViewHolder(val binding: ItemPhotoBinding): RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object: DiffUtil.ItemCallback<SharedStoragePhoto>() {
        override fun areContentsTheSame(
            oldItem: SharedStoragePhoto,
            newItem: SharedStoragePhoto
        ): Boolean {
            return oldItem == newItem
        }
        override fun areItemsTheSame(
            oldItem: SharedStoragePhoto,
            newItem: SharedStoragePhoto
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
    val differ = AsyncListDiffer<SharedStoragePhoto>(this, diffUtil)
    var photos: List<SharedStoragePhoto>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SharedStoragePhotoViewHolder {
        return SharedStoragePhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: SharedStoragePhotoViewHolder, position: Int) {
        val photoItem = photos[position]
        holder.binding.apply{
            photo.setImageURI(photoItem.contentUri)
            val aspectRatio = photoItem.width.toFloat() / photoItem.height.toFloat()
            ConstraintSet().apply {
                clone(root)
                setDimensionRatio(holder.binding.photo.id ,aspectRatio.toString())
                applyTo(root)
            }
            photo.setOnLongClickListener {
                onPhotoLongClickListener(photoItem)
                true
            }
        }
    }
}
