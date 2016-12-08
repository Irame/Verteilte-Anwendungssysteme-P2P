using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Threading;

namespace ChordDisplay
{
    public class MainViewModel : PropertyChangedBase
    {
        FileSystemWatcher fileWatcher;

        string FullFilename { get; set; }
        string FilePath => Path.GetDirectoryName(FullFilename);
        string FileName => Path.GetFileName(FullFilename);

        public ObservableCollection<ChordNode> Nodes
        {
            get;
        } = new ObservableCollection<ChordNode>();


        public MainViewModel()
        {
            ParseCmdArgs();

            fileWatcher = new FileSystemWatcher(FilePath);
            fileWatcher.Changed += FileWatcher_Changed;
            fileWatcher.EnableRaisingEvents = true;

            UpdateRing();
        }

        private void ParseCmdArgs()
        {
            string[] args = Environment.GetCommandLineArgs();

            if (args.Length > 1)
            {
                FullFilename = args[1];
            }
            else
            {
                throw new InvalidOperationException("Please provide the full filename of the file to watch.");
            }
        }

        private void FileWatcher_Changed(object sender, FileSystemEventArgs e)
        {
            if (e.Name == FileName && e.ChangeType == WatcherChangeTypes.Changed)
            {
                Application.Current.Dispatcher.Invoke(UpdateRing);
            }
        }

        private void UpdateRing()
        {
            List<ChordNode> newElements = ReadFile(FullFilename).ToList();

            foreach (var chordNode in Nodes.Where(chordNode => !newElements.Contains(chordNode)).ToList())
            {
                Nodes.Remove(chordNode);
            }

            foreach (var chordNode in newElements.Where(chordNode => !Nodes.Contains(chordNode)))
            {
                Nodes.Add(chordNode);
            }
        }

        private IEnumerable<ChordNode> ReadFile(string fullFilename)
        {
            using (var fileStream = new StreamReader(File.Open(fullFilename, FileMode.Open, FileAccess.Read, FileShare.Read)))
            {
                string line;
                while ((line = fileStream.ReadLine()) != null)
                {
                    var split = line.Split(';');

                    var hash = split[0];
                    var url = split[1];

                    yield return new ChordNode(hash, url);
                }
            }
        }
    }
}
