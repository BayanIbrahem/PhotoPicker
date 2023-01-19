package com.example.photopicker.domain.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.photopicker.databinding.ItemPhotoBinding
import com.example.photopicker.domain.adapters.InternalStoragePhotoAdapter.InternalStoragePhotoViewHolder
import com.example.photopicker.domain.unit_classes.InternalStoragePhoto

class InternalStoragePhotoAdapter (val onPhotoLongClickListener: (photo: InternalStoragePhoto) -> Unit) : RecyclerView.Adapter<InternalStoragePhotoViewHolder>() {

    inner class InternalStoragePhotoViewHolder(val binding: ItemPhotoBinding): RecyclerView.ViewHolder(binding.root)

    val differUtil = object: DiffUtil.ItemCallback<InternalStoragePhoto>() {
        override fun areContentsTheSame(
            oldItem: InternalStoragePhoto,
            newItem: InternalStoragePhoto
        ): Boolean {
            return oldItem.name == newItem.name
        }
        override fun areItemsTheSame(
            oldItem: InternalStoragePhoto,
            newItem: InternalStoragePhoto
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
    val differ = AsyncListDiffer<InternalStoragePhoto>(this, differUtil)
    var photos: List<InternalStoragePhoto>
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
    ): InternalStoragePhotoViewHolder {
        return InternalStoragePhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: InternalStoragePhotoViewHolder, position: Int) {
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