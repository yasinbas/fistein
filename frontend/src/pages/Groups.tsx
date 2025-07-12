import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Users, Search, Filter, Calendar, MoreVertical } from 'lucide-react';
import { groupsAPI } from '../services/api';
import type { Group } from '../types';

const Groups: React.FC = () => {
  const [groups, setGroups] = useState<Group[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterBy, setFilterBy] = useState<'all' | 'admin' | 'member'>('all');

  useEffect(() => {
    const loadGroups = async () => {
      try {
        const groupsData = await groupsAPI.getGroups();
        setGroups(groupsData);
      } catch (error) {
        console.error('Gruplar yüklenirken hata:', error);
      } finally {
        setLoading(false);
      }
    };

    loadGroups();
  }, []);

  const filteredGroups = groups.filter(group => {
    const matchesSearch = group.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         group.description?.toLowerCase().includes(searchTerm.toLowerCase());
    
    // Since we don't have admin info in the current Group type, we'll filter by all for now
    // This can be enhanced when backend provides admin information
    return matchesSearch;
  });

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('tr-TR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Gruplarım</h1>
            <p className="text-gray-600 mt-1">
              Toplam {groups.length} grup
            </p>
          </div>
          <Link
            to="/groups/new"
            className="btn btn-primary flex items-center gap-2"
          >
            <Plus className="h-5 w-5" />
            Yeni Grup
          </Link>
        </div>
      </div>

      {/* Search and Filter */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
            <input
              type="text"
              placeholder="Grup adı veya açıklama ara..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
          
          <div className="flex items-center gap-2">
            <Filter className="h-5 w-5 text-gray-400" />
            <select
              value={filterBy}
              onChange={(e) => setFilterBy(e.target.value as 'all' | 'admin' | 'member')}
              className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="all">Tüm Gruplar</option>
              <option value="admin">Yönetici Olduğum</option>
              <option value="member">Üye Olduğum</option>
            </select>
          </div>
        </div>
      </div>

      {/* Groups List */}
      {filteredGroups.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredGroups.map((group) => (
            <div key={group.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-200">
              <div className="p-6">
                <div className="flex justify-between items-start mb-4">
                  <div className="flex items-center">
                    <div className="p-3 bg-primary-100 rounded-lg mr-3">
                      <Users className="h-6 w-6 text-primary-600" />
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900">
                        {group.name}
                      </h3>
                      <p className="text-sm text-gray-600">
                        {group.members?.length || 0} üye
                      </p>
                    </div>
                  </div>
                  <button className="p-2 hover:bg-gray-100 rounded-lg">
                    <MoreVertical className="h-5 w-5 text-gray-400" />
                  </button>
                </div>

                <p className="text-gray-600 mb-4 line-clamp-2">
                  {group.description || 'Açıklama bulunmuyor'}
                </p>

                <div className="flex items-center justify-between text-sm text-gray-500 mb-4">
                  <div className="flex items-center">
                    <Calendar className="h-4 w-4 mr-1" />
                    {formatDate(group.createdAt)}
                  </div>
                  <div className="text-xs bg-gray-100 px-2 py-1 rounded">
                    {group.createdBy.fullName}
                  </div>
                </div>

                <div className="flex gap-2">
                  <Link
                    to={`/groups/${group.id}`}
                    className="flex-1 btn btn-primary text-center"
                  >
                    Detaylar
                  </Link>
                  <Link
                    to={`/expenses/new?groupId=${group.id}`}
                    className="flex-1 btn btn-secondary text-center"
                  >
                    Harcama Ekle
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow-md p-12 text-center">
          <Users className="h-16 w-16 mx-auto mb-4 text-gray-300" />
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            {searchTerm ? 'Grup bulunamadı' : 'Henüz grup yok'}
          </h3>
          <p className="text-gray-600 mb-6">
            {searchTerm 
              ? 'Arama kriterlerinize uygun grup bulunamadı. Farklı kelimeler deneyin.'
              : 'Arkadaşlarınızla giderlerinizi paylaşmak için ilk grubunuzu oluşturun.'
            }
          </p>
          {!searchTerm && (
            <Link
              to="/groups/new"
              className="btn btn-primary inline-flex items-center gap-2"
            >
              <Plus className="h-5 w-5" />
              İlk Grubunuzu Oluşturun
            </Link>
          )}
        </div>
      )}
    </div>
  );
};

export default Groups;