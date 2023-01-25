package com.example.photopicker.domain.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.photopicker.databinding.ItemPhotoBinding
import com.example.photopicker.domain.utils.adapters.PrivatePhotoAdapter.PrivatePhotoViewHolder
import com.example.photopicker.domain.utils.PrivatePhoto

class PrivatePhotoAdapter (val onPhotoLongClickListener: (photo: PrivatePhoto) -> Unit) : RecyclerView.Adapter<PrivatePhotoViewHolder>() {

    inner class PrivatePhotoViewHolder(val binding: ItemPhotoBinding): RecyclerView.ViewHolder(binding.root)

    val differUtil = object: DiffUtil.ItemCallback<PrivatePhoto>() {
        override fun areContentsTheSame(
            oldItem: PrivatePhoto,
            newItem: PrivatePhoto
        ): Boolean {
            return oldItem.name == newItem.name
        }
        override fun areItemsTheSame(
            oldItem: PrivatePhoto,
            newItem: PrivatePhoto
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
    val differ = AsyncListDiffer<PrivatePhoto>(this, differUtil)
    var photos: List<PrivatePhoto>
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
    ): PrivatePhotoViewHolder {
        return PrivatePhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: PrivatePhotoViewHolder, position: Int) {
        val photoItem = photos[position]
        holder.binding.apply{
            this.photo.setImageBitmap(photoItem.bitmap)
            val aspectRatio = photoItem.bitmap.width.toFloat() / photoItem.bitmap.height.toFloat()
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