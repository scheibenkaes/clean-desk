(ns clean-desk.core
  (:use [clojure.java.io :only [file]])
  (:use [clojure.string :only [split]])
  (:use [clojure.contrib.io :only [file-str]])
  (:import javax.activation.MimetypesFileTypeMap))

(def mime-map (MimetypesFileTypeMap.))

(def desktop (file-str "~/Desktop"))

(def default-dest-dir (file-str desktop "/" "clean"))

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
       (filter #(.isFile %))
       (remove #(-> % .getName (.startsWith ".")))))

(defn make-mime-mapping [folder]
  (let [files (read-folder folder)]
    (reduce (fn [acc f] (update-in acc [(detect-mime f)] conj f)) {} files)))

(defn mime->out [target mime]
  (file target (-> mime (split #"/") first)))

(defn move-files [dest mapping]
  (doseq [[mime files] mapping]
    (let [out (mime->out dest mime)]
      (when-not (.exists out) (.mkdirs out))
      (doseq [f files]
        (let [out-f (file out (.getName f))]
          (println (format "Moving %s to %s" f out-f))
          (.renameTo f out-f))))))

(defn clean-up
  ([] (clean-up desktop default-dest-dir))
  ([folder target]
     (let [mapping (make-mime-mapping folder)]
       (move-files target mapping))))
