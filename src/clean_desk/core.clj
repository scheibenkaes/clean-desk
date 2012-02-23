(ns clean-desk.core
  (:use [clojure.java.io :only [file]])
  (:use [clojure.string :only [split]])
  (:use [clojure.contrib.io :only [file-str]])
  (:import javax.activation.MimetypesFileTypeMap))

(def mime-map (MimetypesFileTypeMap.))

(def desktop (file-str "~/Desktop"))

(defmulti detect-mime class)

(defmethod detect-mime String [^String f]
  (.getContentType mime-map f))

(defmethod detect-mime java.io.File [f]
  (detect-mime (.getName f)))

(defn read-folder [folder-name]
  (->> folder-name
       file
       .listFiles
       seq
       (filter #(.isFile %))))

(defn make-mime-mapping [folder]
  (let [files (read-folder folder)]
    (reduce (fn [acc f] (update-in acc [(detect-mime f)] conj f)) {} files)))

(defn subfolder-names-from-mime-mapping [mapping]
  (reduce (fn [acc m] (conj acc (-> m first (split #"/") first))) #{} mapping))