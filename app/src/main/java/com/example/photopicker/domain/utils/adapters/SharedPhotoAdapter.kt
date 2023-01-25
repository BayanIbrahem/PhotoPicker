package com.example.photopicker.domain.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.photopicker.databinding.ItemPhotoBinding
import com.example.photopicker.domain.utils.SharedPhoto
import com.example.photopicker.domain.utils.adapters.SharedPhotoAdapter.SharedPhotoViewHolder

class SharedPhotoAdapter (val onPhotoLongClickListener: (photo: SharedPhoto) -> Unit) : RecyclerView.Adapter<SharedPhotoViewHolder>() {

    inner class SharedPhotoViewHolder(val binding: ItemPhotoBinding): RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object: DiffUtil.ItemCallback<SharedPhoto>() {
        override fun areContentsTheSame(
            oldItem: SharedPhoto,
            newItem: SharedPhoto
        ): Boolean {
            return oldItem == newItem
        }
        override fun areItemsTheSame(
            oldItem: SharedPhoto,
            newItem: SharedPhoto
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
    val differ = AsyncListDiffer<SharedPhoto>(this, diffUtil)
    var photos: List<SharedPhoto>
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
    ): SharedPhotoViewHolder {
        return SharedPhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: SharedPhotoViewHolder, position: Int) {
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
